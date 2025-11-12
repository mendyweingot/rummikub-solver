package com.mw.rummi.domainObjects;
import java.util.List;

import com.mw.rummi.Rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;


public class TilePile {
    HashMap<PosDomainObject, List<TileDomainObject>> pile = new HashMap<>();

    public TilePile(){
    }

    public ArrayList<TileDomainObject> remove(TileDomainObject tile){
        //not sure what else might be needed in the future. so far it's only needed for locked tile piles
        List<TileDomainObject> tiles = pile.get(tile.pos);
        tiles.remove(tile);
        return null;
    }

    public ArrayList<TileDomainObject> getAllTiles(){
        ArrayList<TileDomainObject> tiles = new ArrayList<>();
        for(List<TileDomainObject> dupTiles: this.pile.values()){
            tiles.addAll(dupTiles);
        }
        return tiles;
    }

    public void addTilesWithUpdatingAffectedTiles(ArrayList<TileDomainObject> tiles) throws Exception {
        for (TileDomainObject t: tiles){
           addTileWithUpdatingAffectedTiles(t);
        }
    }



    public void addTileWithUpdatingAffectedTiles(TileDomainObject tile) throws Exception {
        ArrayList<TileDomainObject> affectedTiles = addTile(tile);

        for (TileDomainObject t: affectedTiles){
            t.front = existsTilesAhead(t);
            t.back = existsTilesBack(t);
            t.mid = existsTilesMid(t);
            t.pair = existsTilesPair(t);
        }
    }

    public ArrayList<TileDomainObject> addTile(TileDomainObject tile) throws Exception{

        if (!pile.containsKey(tile.pos)) pile.put(tile.pos, new ArrayList<>()); //in case it's the first time the pos is added
        List<TileDomainObject> duplicateTiles = pile.get(tile.pos);
        if (duplicateTiles.contains(tile)) throw new Exception("Tile already exists in pile"); //precaution to make sure tile doesn't already exist in pile
        duplicateTiles.add(tile);

        ArrayList<TileDomainObject> affectedTiles = new ArrayList<>(); //obtains all the tiles that may have been affected
        PosDomainObject ahead1 = tile.pos.getPosAhead(); //can't use neededPositionsFor... because it may return null even if one value does exist
        if (ahead1 != null) affectedTiles.addAll(getAllTilesByPos(ahead1));
        PosDomainObject ahead2 = ahead1.getPosAhead();
        if (ahead2 != null) affectedTiles.addAll(getAllTilesByPos(ahead2));
        PosDomainObject back1 = tile.pos.getPosBack();
        if (back1 != null) affectedTiles.addAll(getAllTilesByPos(back1));
        PosDomainObject back2 = back1.getPosBack();
        if (back2 != null) affectedTiles.addAll(getAllTilesByPos(back2));
        for (PosDomainObject pos: neededPositionsForPair(tile)) affectedTiles.addAll(getAllTilesByPos(pos));
        affectedTiles.addAll(getAllTilesByPos(tile.pos)); //the added tile and its duplicates may also need to be updated
        return affectedTiles;
    }

    public ArrayList<TileDomainObject> getAllTilesByPos(PosDomainObject pos){
        if (!pile.containsKey(pos)) return new ArrayList<>(); //return an empty arrayList of Pos doesn't exist
        return (ArrayList<TileDomainObject>)pile.get(pos);
        //old code
        /*ArrayList<Tile> tiles = new ArrayList<>();
        for (Tile tile: pile[getTilePositionInPile(pos)]){
            if (tile != null) tiles.add(tile); //make sure no null values are added
        }
        return tiles;*/
    }

    public TileDomainObject[] getTilesAhead(TileDomainObject tile, List<TileDomainObject> unwantedTiles){
        PosDomainObject[] positions = neededPositionsForAhead(tile);
        if (positions == null) return null; //if wrap around is not enabled, tiles may not exist
        TileDomainObject tile1 = getTilebyPos(positions[0], unwantedTiles);
        TileDomainObject tile2 = getTilebyPos(positions[1], unwantedTiles);
        if (tile1 == null || tile2 == null) return null;
        return new TileDomainObject[] {tile1, tile2};
    }
    
    public TileDomainObject[] getTilesAhead(TileDomainObject tile, TileDomainObject... unwantedTiles){
        return getTilesAhead(tile, new ArrayList<>(List.of(unwantedTiles)));
    }

    public TileDomainObject[] getTilesBack(TileDomainObject tile, TileDomainObject... unwantedTiles){
        PosDomainObject[] positions = neededPositionsForBack(tile);
        if (positions == null) return null; //if wrap around is not enabled, tiles may not exist
        TileDomainObject tile1 = getTilebyPos(positions[0], unwantedTiles);
        TileDomainObject tile2 = getTilebyPos(positions[1], unwantedTiles);
        if (tile1 == null || tile2 == null) return null;
        return new TileDomainObject[] {tile1, tile2};
    }

    public TileDomainObject[] getTilesMid(TileDomainObject tile, TileDomainObject... unwantedTiles){
        PosDomainObject[] positions = neededPositionsForMid(tile);
        if (positions == null) return null; //if wrap around is not enabled, tiles may not exist
        TileDomainObject tile1 = getTilebyPos(positions[0], unwantedTiles);
        TileDomainObject tile2 = getTilebyPos(positions[1], unwantedTiles);
        if (tile1 == null || tile2 == null) return null;
        return new TileDomainObject[] {tile1, tile2};
    }

    public TileDomainObject[] getTilesPairs(TileDomainObject tile, TileDomainObject... unwantedTiles){
        //only two of pairs will be returned
        TileDomainObject[] tiles = new TileDomainObject[2];
        PosDomainObject[] positions = neededPositionsForPair(tile);
        int i = 0;
        for (PosDomainObject pos: positions){
            if (i == 2) break; //two pairs has been found
            TileDomainObject t = getTilebyPos(pos, unwantedTiles);
            if (t != null){
                tiles[i] = t; //a pair has been found
                i++;
            }
        }
        return (i == 2)? tiles: null; //make sure two pairs has been found
    }

    public TileDomainObject getTilebyPos(PosDomainObject pos, TileDomainObject... unwantedTiles){
        return getTilebyPos(pos, new ArrayList<>(List.of(unwantedTiles)));
    }

    public TileDomainObject getTilebyPos(PosDomainObject pos, List<TileDomainObject> unwantedTiles){
        List<TileDomainObject> tiles = pile.get(pos);
        if (tiles == null) return null; //in case pos doesn't exist in the hashmap
        for (TileDomainObject tile: pile.get(pos)){
            if (!unwantedTiles.contains(tile)) return tile; //only the first matching tile will be returned (not any eligable dups)
        }
        return null;
    }

    public int existsTilesAhead(TileDomainObject tile){
        PosDomainObject[] positions = neededPositionsForAhead(tile);
        if (positions == null) return 0; //if wrap around is not enabled, tiles may not exist
        int numberOfTilesPosition1 = existsDuplicate(positions[0]);
        int numberOfTilesPosition2 = existsDuplicate(positions[1]);
        return Math.min(numberOfTilesPosition1, numberOfTilesPosition2);
    }

    public int existsTilesBack(TileDomainObject tile){
        PosDomainObject[] positions = neededPositionsForBack(tile);
        if (positions == null) return 0; //if wrap around is not enabled, tiles may not exist
        int numberOfTilesPosition1 = existsDuplicate(positions[0]);
        int numberOfTilesPosition2 = existsDuplicate(positions[1]);
        return Math.min(numberOfTilesPosition1, numberOfTilesPosition2);
    }

    public int existsTilesMid(TileDomainObject tile){
        PosDomainObject[] positions = neededPositionsForMid(tile);
        if (positions == null) return 0; //if wrap around is not enabled, tiles may not exist
        int numberOfTilesPosition1 = existsDuplicate(positions[0]);
        int numberOfTilesPosition2 = existsDuplicate(positions[1]);
        return Math.min(numberOfTilesPosition1, numberOfTilesPosition2);
    }

    public double existsTilesPair(TileDomainObject tile){
        PosDomainObject[] positions = neededPositionsForPair(tile);
        int positionWithHighestNumberOfDups = 0; //in the case of duplicates allowed, if one position makes up for more than half of eligable positions, those extra positions aren't counted
        int sum = 0;
        for (PosDomainObject pos: positions){ //number of colors can vary
            int numberOfTilesInPosition = existsDuplicate(pos);
            if (numberOfTilesInPosition > positionWithHighestNumberOfDups) positionWithHighestNumberOfDups = numberOfTilesInPosition;
            sum += numberOfTilesInPosition;
        }
        sum = (positionWithHighestNumberOfDups * 2 > sum)? (sum - positionWithHighestNumberOfDups) * 2: sum; //potential trouble: if there are only 1 or 2 of dups, no points will be awarded, is that good?
        double total = (sum == 0)? 0: sum / 2.0; //avoid dividing by zero
        return total;
    }

    public int existsDuplicate(TileDomainObject tile){
        return existsDuplicate(tile.pos);
    }
    //including self
    public int existsDuplicate(PosDomainObject pos){
        List<TileDomainObject> dups = pile.get(pos);
        return (dups == null)? 0: dups.size(); //in case the list doesn't exist
    }
    //old code
    private int getTilePositionInPile(TileDomainObject tile){
        return getTilePositionInPile(tile.pos);
    }
    //old code
    private int getTilePositionInPile(PosDomainObject pos){
        return pos.num * pos.color.getValue();
    }

    public static PosDomainObject[] neededPositionsForAhead(TileDomainObject tile){
        PosDomainObject[] positions = new PosDomainObject[2];
        positions[0] = tile.pos.getPosAhead();
        if (positions[0] == null) return null; //if wraparound is not enabled
        positions[1] = positions[0].getPosAhead();
        if (positions[1] == null) return null;
        return positions;
    }

    public static PosDomainObject[] neededPositionsForBack(TileDomainObject tile){
        PosDomainObject[] positions = new PosDomainObject[2];
        positions[0] = tile.pos.getPosBack();
        if (positions[0] == null) return null; //if wraparound is not enabled
        positions[1] = positions[0].getPosBack();
        if (positions[1] == null) return null;
        return positions;
    }

    public static PosDomainObject[] neededPositionsForMid(TileDomainObject tile){
        PosDomainObject[] positions = new PosDomainObject[2];
        positions[0] = tile.pos.getPosAhead();
        positions[1] = tile.pos.getPosBack();
        if (positions[0] == null || positions[1] == null) return null; //if wraparound is not enabled
        return positions;
    }

    public static PosDomainObject[] neededPositionsForPair(TileDomainObject tile){
        PosDomainObject[] positions = new PosDomainObject[Rules.numberOfColors-1];
        int i = 0;
        for (PosDomainObject pos: tile.pos.getPosPairs()){
            positions[i] = pos;
            i++;
        }
        return positions;
    }
    
}
