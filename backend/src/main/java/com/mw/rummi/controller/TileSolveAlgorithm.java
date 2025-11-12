package com.mw.rummi.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.mw.rummi.domainObjects.*;

public class TileSolveAlgorithm {
    
    public TilePile board;
    public TilePile rack;
    public TilePile tiles;
    public ArrayList<TileSet> boardSets;
    private Process steps;

    public TileSolveAlgorithm(TilePile board, TilePile rack, ArrayList<TileSet> boardSets) throws Exception{

        this.board = board;
        this.rack = rack;
        this.boardSets = boardSets;

        this.steps = new Process();

        TilePile tiles = new TilePile();
        tiles.addTilesWithUpdatingAffectedTiles(board.getAllTiles());
        tiles.addTilesWithUpdatingAffectedTiles(rack.getAllTiles());
        this.tiles = tiles;
        //this should be used to obtain board and rack
        //add them together to set up a new tile piles = tiles
        //create the locked tiles list and locked tilepile
        //initiate the algorithm

        //if tiles can be successfully placed. make sure the board and rack reflect changes
    }

    public Process findPlaceForTile(TileDomainObject tile) throws Exception{
        this.tiles.addTileWithUpdatingAffectedTiles(tile);
        ArrayList<TileDomainObject> looseTiles = new ArrayList<>();
        ArrayList<TileDomainObject> lockedTiles = new ArrayList<>();
        TilePile lockedTilePile = new TilePile();
        looseTiles.addLast(tile);
        findPlaceForTile(looseTiles, lockedTiles, lockedTilePile);

        this.steps.steps.reversed();
        return this.steps;
    }

    
    //may need to keep a separate list of all the tiles added from the rack to the board so that all tiles can be updated accordingly
    private boolean findPlaceForTile(ArrayList<TileDomainObject> loosetiles, ArrayList<TileDomainObject> lockedTiles, TilePile lockedTilePile) throws Exception{

        if (loosetiles.size() == 0) return true; //yay! all tiles found their place

        TileDomainObject tile = loosetiles.removeFirst();

        if (tile.getTotal() < 1) return false; 

        Ahead: {

            if (tile.front < 1) break Ahead;//remmeber that tiles is a combination of board and rack. so if there's a way to get rid of the tile (total is not zero), but there's a duplicate on the rack, the total may be zero.

            if (tryAddingDirectToLockedSet(tile, tile.pos.getPosAhead(), loosetiles, lockedTiles, lockedTilePile)) {
                this.steps.addStep(tile);
                return true;
            } 
            if (tryAddingIndirectToLockedSet(tile, tile.pos.getPosAhead(), tile.pos.getPosAhead().getPosAhead(), loosetiles, lockedTiles, lockedTilePile)) {
                this.steps.addStep(tile);
                return true;
            }

            //try pulling two tiles to create a new locked set
            TileDomainObject tile1 = getTileByPosWithAPreferenceOnBoardTiles(tile.pos.getPosAhead(), lockedTiles); //get the next tile
            TileDomainObject tile2 = getTileByPosWithAPreferenceOnBoardTiles(tile.pos.getPosAhead().getPosAhead(), lockedTiles); //get the next tile
            if (tile1 != null && tile2 != null){
                if (tryPullingTwoTilesToCreateANewLockedSet(tile, tile1, tile2, loosetiles, lockedTiles, lockedTilePile)) {
                    this.steps.addStep(tile);
                    return true; 
                }
            }
                       
        }

        behind: {

            if (tile.back < 1) break behind;

            if (tryAddingDirectToLockedSet(tile, tile.pos.getPosBack(), loosetiles, lockedTiles, lockedTilePile)) {
                this.steps.addStep(tile);
                return true;
            }
            if (tryAddingIndirectToLockedSet(tile, tile.pos.getPosBack(), tile.pos.getPosBack().getPosBack(), loosetiles, lockedTiles, lockedTilePile)) {
                this.steps.addStep(tile);
                return true;
            }

            //try pulling two tiles to create a new locked set
            TileDomainObject tile1 = getTileByPosWithAPreferenceOnBoardTiles(tile.pos.getPosBack(), lockedTiles); //get the next tile
            TileDomainObject tile2 = getTileByPosWithAPreferenceOnBoardTiles(tile.pos.getPosBack().getPosBack(), lockedTiles); //get the next tile
            if (tile1 != null && tile2 != null){
                if (tryPullingTwoTilesToCreateANewLockedSet(tile, tile1, tile2, loosetiles, lockedTiles, lockedTilePile)) {
                    this.steps.addStep(tile);
                    return true; 
                }
            }            

        }

        //mid should be after behind, so that ahead and behind will check if tile can be added to a set containing locked tiles. at this point mid definately won't work
        mid: {

            if (tile.mid < 1) break mid;

            //try pulling two tiles to create a new locked set
            TileDomainObject tile1 = getTileByPosWithAPreferenceOnBoardTiles(tile.pos.getPosBack(), lockedTiles); //get the next tile
            TileDomainObject tile2 = getTileByPosWithAPreferenceOnBoardTiles(tile.pos.getPosAhead(), lockedTiles); //get the next tile
            if (tile1 != null && tile2 != null){
                if (tryPullingTwoTilesToCreateANewLockedSet(tile, tile1, tile2, loosetiles, lockedTiles, lockedTilePile)) {
                    this.steps.addStep(tile);
                    return true;
                }
            }  
        }

        pair: {

            if (tile.pair < 1) break pair;

            if (tryPairingToAllLockedTileSetsOfEligablePairs(tile, loosetiles, lockedTiles, lockedTilePile)) {
                this.steps.addStep(tile);
                return true;
            }
            if (tryParingANewTileSetWithAllPossiblePairCombinations(tile, loosetiles, lockedTiles, lockedTilePile)) {
                this.steps.addStep(tile);
                return true;
            }

        }

        loosetiles.addLast(tile);
        return false;
    }


    private boolean tryAddingDirectToLockedSet(TileDomainObject tile, PosDomainObject lockedTilePos, ArrayList<TileDomainObject> loosetiles, ArrayList<TileDomainObject> lockedTiles, TilePile lockedTilePile) throws Exception {

        ArrayList<TileDomainObject> lockedTilesNext = lockedTilePile.getAllTilesByPos(lockedTilePos);

        TileSet setDirect = null;
        for (TileDomainObject lockedTile: lockedTilesNext){
            if (lockedTile.set.canAdd(tile)){
                setDirect = lockedTile.set;
            }
        }

        if (setDirect == null) return false; //no eligable locked sets were found

        boolean successful = setDirect.add(tile); //stored in a boolean just for debugging purposes

        lockedTiles.add(tile); //since we found a place for the tile, it should be locked there for now to prevent loops
        lockedTilePile.addTile(tile);

        //continue with recursion until all loose tiles are taken care of
        boolean successfullyGotRidOfAllLooseTiles = findPlaceForTile(loosetiles, lockedTiles, lockedTilePile);
        if (successfullyGotRidOfAllLooseTiles) return true;

        //if previous statement false, it means all future recursions couldn't yield a good configuration of the board, and we must undo this step

        lockedTiles.remove(tile);
        lockedTilePile.remove(tile);

        //restore the locked set to its previous condition.
        ArrayList<TileDomainObject> brokenSet = setDirect.breakupSet();
        brokenSet.remove(tile);
        setDirect.addAll(brokenSet);
        
        return false;
    }


    private boolean tryAddingIndirectToLockedSet(TileDomainObject tile, PosDomainObject intermediatePos, PosDomainObject lockedTilePos, ArrayList<TileDomainObject> loosetiles, ArrayList<TileDomainObject> lockedTiles, TilePile lockedTilePile) throws Exception{

        TileDomainObject intermediateTile = getTileByPosWithAPreferenceOnBoardTiles(intermediatePos, lockedTiles); //get the next tile
        if (intermediateTile == null) return false; //no next tile exists

        TileSet setOfIntermediateTile = intermediateTile.set;
        TileSet setIndirect = checkIfTileCanBeAddedToSetOfLockedTileWithIntermediate(tile,  lockedTilePile.getAllTilesByPos(lockedTilePos), intermediateTile); //keep in mind that intermediateTile.set will be cleared with this operation
        if (setIndirect == null) {
            intermediateTile.set = setOfIntermediateTile; //this was undone in the operation two lines above
            return false; //can't be added to an indirect set
        }
        
        //if intermediate tile was part of a set, it must be broken up, and the other tiles from the set added to loose tiles
        ArrayList<TileDomainObject> brokenTilesFromIntermediateTileSet = new ArrayList<>();
        if (setOfIntermediateTile != null){ //the intermediate tile is part of a set
            brokenTilesFromIntermediateTileSet.addAll(setOfIntermediateTile.breakupSet());
            boardSets.remove(setOfIntermediateTile); //remove from list of sets on the board
            brokenTilesFromIntermediateTileSet.remove(intermediateTile);
        }        
        loosetiles.addAll(brokenTilesFromIntermediateTileSet);    

        //in case the intermediate tile is taken from the loose tiles, it must be removed from there
        boolean wasIntermediateTileInLooseTiles = false;
        if (loosetiles.contains(intermediateTile)){
            wasIntermediateTileInLooseTiles = true;
            loosetiles.remove(intermediateTile); // in case it was in the loose tiles list
        }

        setIndirect.addAll(tile, intermediateTile); //add to the locked set

        lockedTiles.add(tile); 
        lockedTiles.add(intermediateTile);
        lockedTilePile.addTile(tile); 
        lockedTilePile.addTile(intermediateTile);    


        //continue with recursion until all loose tiles are taken care of
        boolean successfullyGotRidOfAllLooseTiles = findPlaceForTile(loosetiles, lockedTiles, lockedTilePile);
        if (successfullyGotRidOfAllLooseTiles) return true;

        //if previous statement false, it means all future recursions couldn't yield a good configuration of the board, and we must undo this step

        lockedTiles.remove(tile);
        lockedTiles.remove(intermediateTile);
        lockedTilePile.remove(tile);
        lockedTilePile.remove(intermediateTile);


        //restore the locked set to its previous condition.
        ArrayList<TileDomainObject> brokenSet = setIndirect.breakupSet();
        brokenSet.remove(tile);
        brokenSet.remove(intermediateTile);
        setIndirect.addAll(brokenSet);

        //if intermediate tile was taken from the loose tile list, it must be put back there
        if (wasIntermediateTileInLooseTiles) { 
            loosetiles.addLast(intermediateTile);
        }

        //if the intermediate tile was part of set, it must be put back together, and all other tiles from the set removed from the loose tiles list
        if (setOfIntermediateTile != null){
            loosetiles.removeAll(brokenTilesFromIntermediateTileSet);
            brokenTilesFromIntermediateTileSet.add(intermediateTile);
            setOfIntermediateTile.addAll(brokenTilesFromIntermediateTileSet);
            boardSets.add(setOfIntermediateTile);//add the set back to the list of board sets
        }
        
        return false;
    }

    private boolean tryPullingTwoTilesToCreateANewLockedSet(TileDomainObject tile, TileDomainObject tile1, TileDomainObject tile2, ArrayList<TileDomainObject> loosetiles, ArrayList<TileDomainObject> lockedTiles, TilePile lockedTilePile) throws Exception{

        TileSet tile1Set = tile1.set;
        TileSet tile2Set = tile2.set;
   
        //if tile1 was part of a set, it must be broken up, and the other tiles from the set added to loose tiles
        ArrayList<TileDomainObject> brokenTilesFromTile1Set = new ArrayList<>();
        if (tile1Set != null){ //tile1 is part of a set
            brokenTilesFromTile1Set.addAll(tile1Set.breakupSet());
            boardSets.remove(tile1Set);
            brokenTilesFromTile1Set.remove(tile1);
        }        
        loosetiles.addAll(brokenTilesFromTile1Set);    

        //if tile2 was part of a set, it must be broken up, and the other tiles from the set added to loose tiles
        ArrayList<TileDomainObject> brokenTilesFromTile2Set = new ArrayList<>();
        if (tile2Set != null && tile1Set != tile2Set){ //if tile1 and tile2 are part of the same set, it was already broken up earlier by tile1
            brokenTilesFromTile2Set.addAll(tile2Set.breakupSet());
            boardSets.remove(tile2Set);
            brokenTilesFromTile2Set.remove(tile2);
        }        
        loosetiles.addAll(brokenTilesFromTile2Set);   

        //in case tile1 is taken from the loose tiles, it must be removed from there
        boolean wasTile1InLooseTiles = false;
        if (loosetiles.contains(tile1)){
            wasTile1InLooseTiles = true;
            loosetiles.remove(tile1); // in case it was in the loose tiles list
        }

        //in case tile2 is taken from the loose tiles, it must be removed from there
        boolean wasTile2InLooseTiles = false;
        if (loosetiles.contains(tile2)){
            wasTile2InLooseTiles = true;
            loosetiles.remove(tile2); // in case it was in the loose tiles list
        }

        TileSet newTileSet = new TileSet(tile, tile1, tile2); //create a new set containing the tile and two tiles ahead
        boardSets.add(newTileSet); //add to list of sets in the board


        //lock all three tiles
        lockedTiles.add(tile); 
        lockedTiles.add(tile1);
        lockedTiles.add(tile2);
        lockedTilePile.addTile(tile); 
        lockedTilePile.addTile(tile1);    
        lockedTilePile.addTile(tile2); 


        //continue with recursion until all loose tiles are taken care of
        boolean successfullyGotRidOfAllLooseTiles = findPlaceForTile(loosetiles, lockedTiles, lockedTilePile);
        if (successfullyGotRidOfAllLooseTiles) return true;

        //if previous statement false, it means all future recursions couldn't yield a good configuration of the board, and we must undo this step

        lockedTiles.remove(tile); 
        lockedTiles.remove(tile1);
        lockedTiles.remove(tile2);
        lockedTilePile.remove(tile); 
        lockedTilePile.remove(tile1);    
        lockedTilePile.remove(tile2); 


        //breakup the newly created set of three tiles
        newTileSet.breakupSet();
        boardSets.remove(newTileSet);

        //if tile1 was taken from the loose tile set, it must be put back there
        if (wasTile1InLooseTiles) { 
            loosetiles.addLast(tile1);
        }

        //if tile2 was taken from the loose tile set, it must be put back there
        if (wasTile2InLooseTiles) { 
            loosetiles.addLast(tile2);
        }

        //if tile1 was part of set, it must be put back together, and all other tiles from the set removed from the loose tiles list and the set reconstituted
        if (tile1Set != null){
            loosetiles.removeAll(brokenTilesFromTile1Set);
            brokenTilesFromTile1Set.add(tile1);
            tile1Set.addAll(brokenTilesFromTile1Set);
            boardSets.add(tile1Set);
        }

        //if tile2 was part of set, it must be put back together, and all other tiles from the set removed from the loose tiles list and the set reconstituted
        if (tile2Set != null && tile1Set != tile2Set){ //if they came from the same set, it was already put back together by tile1
            loosetiles.removeAll(brokenTilesFromTile2Set);
            brokenTilesFromTile2Set.add(tile2);
            tile2Set.addAll(brokenTilesFromTile2Set);
            boardSets.add(tile2Set);
        }
        
        return false;
    }

    private boolean tryPairingToAllLockedTileSetsOfEligablePairs(TileDomainObject tile, ArrayList<TileDomainObject> loosetiles, ArrayList<TileDomainObject> lockedTiles, TilePile lockedTilePile) throws Exception {

        HashSet<TileSet> eligableSets = getAllTileSetsOfLockedPairSetsATileCanPairTo(tile, lockedTilePile);

        for (TileSet set: eligableSets){
            set.add(tile);
            lockedTiles.add(tile);
            lockedTilePile.addTile(tile);

            //continue with recursion until all loose tiles are taken care of
            boolean successfullyGotRidOfAllLooseTiles = findPlaceForTile(loosetiles, lockedTiles, lockedTilePile);
            if (successfullyGotRidOfAllLooseTiles) return true;

            //if unsucessful, undo everything and try the next TileSet next iteration
            lockedTiles.remove(tile);
            lockedTilePile.remove(tile);

            ArrayList<TileDomainObject> brokenTiles = set.breakupSet();
            brokenTiles.remove(tile);
            set.addAll(brokenTiles);
        }

        return false;
    }

    private boolean tryParingANewTileSetWithAllPossiblePairCombinations(TileDomainObject tile, ArrayList<TileDomainObject> loosetiles, ArrayList<TileDomainObject> lockedTiles, TilePile lockedTilePile) throws Exception{

        ArrayList<TileDomainObject> tilePairs = getAllUnlockedTilesATileCanPairWith(tile, lockedTiles);
        if (tilePairs.size() < 2) return false; //impossible to make a set without at least 2 tiles

        for (int i = 0; i < tilePairs.size()-1; i++){ //for all combinations of every two colors
            for (int j = i+1; j < tilePairs.size(); j++){
                TileDomainObject tile1 = tilePairs.get(i);
                TileDomainObject tile2 = tilePairs.get(j);
                if (tryPullingTwoTilesToCreateANewLockedSet(tile, tile1, tile2, loosetiles, lockedTiles, lockedTilePile)) return true;
            }
        }

        return false;
    }

    //gets all the unlocked possible pairs of a tile (duplicates are ignored)
    private ArrayList<TileDomainObject> getAllUnlockedTilesATileCanPairWith(TileDomainObject tile, ArrayList<TileDomainObject> lockedTiles){
        ArrayList<TileDomainObject> pairTiles = new ArrayList<>();

        for (PosDomainObject pos: tile.pos.getPosPairs()){
            TileDomainObject t = getTileByPosWithAPreferenceOnBoardTiles(pos, lockedTiles);
            if (t != null){
                pairTiles.add(t);
            }            
        }

        return pairTiles;
    }

    //get all the sets of locked tiles in which a tile can be added to them as part of a pair
    private HashSet<TileSet> getAllTileSetsOfLockedPairSetsATileCanPairTo(TileDomainObject tile, TilePile lockedTilePile){
        HashSet<TileSet> sets = new HashSet<>();

        for (PosDomainObject pos: tile.pos.getPosPairs()){
            ArrayList<TileDomainObject> lockedTiles = lockedTilePile.getAllTilesByPos(pos);
            for (TileDomainObject lockedTile: lockedTiles){
                TileSet set = lockedTile.set;
                if (set.canAdd(tile)) sets.add(set);
            }
        }

        return sets;
    }

    private TileDomainObject getTileByPosWithAPreferenceOnBoardTiles(PosDomainObject pos, ArrayList<TileDomainObject> lockedTiles){ //taking a tile from the rack when there's a duplicate on the board can cause trouble. always try to take from the board first
        TileDomainObject tile = board.getTilebyPos(pos, lockedTiles);
        if (tile == null){
            tile = rack.getTilebyPos(pos, lockedTiles);
        }
        return tile;
    }

    private TileSet checkIfTileCanBeAddedToSetOfLockedTileWithIntermediate(TileDomainObject tile, ArrayList<TileDomainObject> lockedTilesTwoNext, TileDomainObject notLockedTileNext){

        //check if tile can be added to a set containing a locked tile one two positions apart from the tile, using another tile
        for (TileDomainObject lockedTileTwoNext: lockedTilesTwoNext){
            TileSet set = lockedTileTwoNext.set;
            if (set.canAddAll(tile, notLockedTileNext)) return set;
        }

        return null; //returns null if tile can't be added to set of locked tile
    }

    public class Process {
        public ArrayList<StepInProcess> steps;

        public Process(){
            steps = new ArrayList<>();
        }

        public void addStep(TileDomainObject tileToGetRidOf, ArrayList<TileDomainObject> tiles){
            steps.add(new StepInProcess(tileToGetRidOf, tiles));
        }

        public void addStep(TileDomainObject tileToGetRidOf, TileDomainObject... tilesInNewSet){
            addStep(tileToGetRidOf, tilesInNewSet);
        }

        public void addStep(TileDomainObject tileToGetRidOf){
            addStep(tileToGetRidOf, tileToGetRidOf.set.getAllTilesInSet());
        }

        public void removeStep(){
            steps.removeLast();
        }



        public class StepInProcess{
            public TileDomainObject tileToGetRidOf;
            public ArrayList<TileDomainObject> tilesInNewSet;

            public StepInProcess(TileDomainObject tileToGetRidOf, ArrayList<TileDomainObject> tiles){
                ArrayList<TileDomainObject> tilesInNewSet = new ArrayList<>();
                for (TileDomainObject t: tiles){
                    tilesInNewSet.add(t);
                }
                this.tileToGetRidOf = tileToGetRidOf;
                this.tilesInNewSet = tilesInNewSet;
            }

            
            public StepInProcess(TileDomainObject tileToGetRidOf, TileDomainObject... tilesInNewSet){
                this.tileToGetRidOf = tileToGetRidOf;
                this.tilesInNewSet = new ArrayList<>(List.of(tilesInNewSet));
            }
        }
    }
}
