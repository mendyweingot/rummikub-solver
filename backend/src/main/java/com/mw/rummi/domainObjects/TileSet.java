package com.mw.rummi.domainObjects;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.mw.rummi.Rules;

import java.util.HashSet;
public class TileSet {
    public static final int RUN = 1;
    public static final int PAIR = 2;

    private ArrayList<TileDomainObject> tiles;
    private int setType;

    public TileSet(TileDomainObject... tiles){
        validateSet(tiles);
    }

    public TileSet(ArrayList<TileDomainObject> tiles){
        validateSet(tiles);
    }

    public boolean isValidSetInOrder(ArrayList<TileDomainObject> tiles){
        if (tiles.size() < 3) return false; //set is too small

        this.setType = (tiles.get(0).pos.color.equals(tiles.get(1).pos.color))? TileSet.RUN: TileSet.PAIR; //determines set type by checking if two tiles have the same color

        TileDomainObject firstTile = tiles.removeFirst();
        this.tiles.add(firstTile);

        if (this.setType == TileSet.PAIR){
            for (TileDomainObject tile: tiles){
                if (!this.addToPair(tile)) return false;
            }
        }
        else {
            for (TileDomainObject tile: tiles){
                if (this.tiles.size() == Rules.highestNumber) return false; //is inevitably a duplicate position
                PosDomainObject lastPos = this.tiles.getLast().pos; 
                if (!(lastPos.getPosAhead() == null || !lastPos.getPosAhead().equals(tile.pos))){ //if wrap is not enabled tile can't be added after last Pos, and tile must follow from the last one both in color and position
                    this.tiles.addLast(tile);
                }
                else {
                    return false;
                }
            }
        }

        return true;
    }

    public ArrayList<TileDomainObject>[] remove(TileDomainObject tile){ // if it's a pair set: it will return all the other tiles in the first index. if it's a run set: it will return the tiles positioned before the removed tile in the first index, and the latter positioned tiles in the second index. (this will help for quick set construction checkups)
        ArrayList<TileDomainObject>[] returnedTiles = new ArrayList[2];
        if (this.getSetType() == TileSet.PAIR){
            returnedTiles[0] = breakupSet();
        }
        else{
            int indexOfRemovedTile = this.tiles.indexOf(tile);
            returnedTiles[0] = (ArrayList<TileDomainObject>)this.tiles.subList(0, indexOfRemovedTile);
            returnedTiles[1] = (ArrayList<TileDomainObject>)this.tiles.subList(indexOfRemovedTile + 1, this.tiles.size());
            breakupSet();
        }
        return returnedTiles;
    }
    //doesn't look like will be used
    private ArrayList<TileDomainObject> removeFromPair(TileDomainObject... tiles){ //will return the now setless tiles
        ArrayList<TileDomainObject> tilesThatAreInSet = new ArrayList<>();//make sure that tiles to be removed are actually in the set
        for (TileDomainObject tile: tiles){
            if (this.tiles.contains(tile)) tilesThatAreInSet.add(tile);
        }
        if (this.tiles.size() - tilesThatAreInSet.size() >= 3) { //the set can continue to exist after removal
            for (TileDomainObject tile: tilesThatAreInSet){
                removeTileSetFromTile(tile);
                this.tiles.remove(tile);
            }
            return null;
        }
        else{ //the set can't continue to exist after removal
            return breakupSet();
        }
    }

    public ArrayList<TileDomainObject> addAll(TileDomainObject... tiles){
        ArrayList<TileDomainObject> sortedTiles = new ArrayList<>(List.of(tiles));
        return addAll(sortedTiles);
    }

    public ArrayList<TileDomainObject> addAll(ArrayList<TileDomainObject> sortedTiles){
        //this is used for reconstituting a tileset after it has been broken up, but we want the same reference
        if (this.tiles == null) {
            validateSet(sortedTiles);
            return null;
        }

        sortedTiles.sort((t1, t2) -> t1.pos.num - t2.pos.num);
        ArrayList<TileDomainObject> rejectedTiles = new ArrayList<>();

        boolean reachedTheEndOfForwardRun = false; //***NEW*** in case of 1, 2, 12, 13. will go from 1-2, and then reverse backward from 13-12

        int index = 0;
        for (int i = 0; i < sortedTiles.size(); i++){//see explanation to this loop in validateSet function
            TileDomainObject tile = sortedTiles.get(index);
            if (!this.add(tile)){
                if (!reachedTheEndOfForwardRun){ //assumes the tile is 13, and now the list has to be reversed **see 4 lines above
                    reachedTheEndOfForwardRun = true;
                    index = sortedTiles.size(); //start calculating backwards
                    i--; //the tile may be tile 12 in example above, and will be calculated later
                }
                else {
                    rejectedTiles.add(tile);
                }                
            }
            index += (reachedTheEndOfForwardRun)? -1: 1;
        }
        for (TileDomainObject tile: sortedTiles){
            if (!add(tile)) rejectedTiles.add(tile);
        }
        return rejectedTiles;
    }

    public boolean add(TileDomainObject tile){
        return (this.setType == TileSet.RUN)? addToRun(tile): addToPair(tile);
    }

    public int getSetType(){
        return setType;
    }

    public ArrayList<TileDomainObject> breakupSet(){
        this.setType = 0;
        ArrayList<TileDomainObject> looseTiles = new ArrayList<>();
        for (TileDomainObject tile: tiles){
            removeTileSetFromTile(tile);
            looseTiles.add(tile);
        }
        this.tiles = null;
        return looseTiles;
    }

    public boolean isValidSet(){
        return this.tiles != null;
    }

    public boolean canAddAll(TileDomainObject... tiles){//after this method, all tiles will have its set removed from the tile
        ArrayList<TileDomainObject> sortedTiles = new ArrayList<TileDomainObject>(List.of(tiles));
        sortedTiles.sort((t1, t2) -> t1.pos.num - t2.pos.num);
        ArrayList<TileDomainObject> successfullyAddedTiles = new ArrayList<>();
        boolean canAddAll = true;

        boolean reachedTheEndOfForwardRun = false; //***NEW*** what if the set is 12, 13, 1. we must try from both ends in case of 2, 3 and 10, 11

        int index = 0;
        for (int i = 0; i < sortedTiles.size(); i++){ //to see explanation for this loop, see the validateSet function
            TileDomainObject tile = sortedTiles.get(index);
            if (this.add(tile)){
                successfullyAddedTiles.add(tile);
            }
            else {
                if (!reachedTheEndOfForwardRun){ //assumes the tile is 13, and now the list has to be reversed **see 4 lines above
                    reachedTheEndOfForwardRun = true;
                    index = sortedTiles.size(); //start calculating backwards
                    i--; //the tile may be tile 12 in example above, and will be calculated later
                }
                else {
                    canAddAll = false;
                    break;
                }                
            }
            index += (reachedTheEndOfForwardRun)? -1: 1;
        }

        for (TileDomainObject tileToBeRemoved: successfullyAddedTiles){
            removeTileSetFromTile(tileToBeRemoved);//remove the set from all the tiles that were successfully added
        }
        this.tiles.removeAll(successfullyAddedTiles);//remove the tiles from the set
        
        return canAddAll;
    }

    public boolean canAdd(TileDomainObject tile){
        return (this.setType == TileSet.RUN)? canAddToRun(tile): canAddToPair(tile);
    }

    private boolean canAddToPair(TileDomainObject tile){
        if (this.tiles.getFirst().pos.num != tile.pos.num) return false; //has to be the same number
        for (TileDomainObject t: this.tiles){
            if (t.pos.equals(tile.pos)) return false; //can't be duplicate positions
        }
        return true;
    }

    private boolean canAddToRun(TileDomainObject tile){
        if (this.tiles.size() == Rules.highestNumber) return false; //is inevitably a duplicate position
        PosDomainObject lastPos = this.tiles.getLast().pos; 
        boolean canBeAddedToEnd = !(lastPos.getPosAhead() == null || !lastPos.getPosAhead().equals(tile.pos)); //if wrap is not enabled tile can't be added after last Pos, and tile must follow from the last one both in color and position
        PosDomainObject firstPos = this.tiles.getFirst().pos;
        boolean canBeAddedToStart = !(firstPos.getPosBack() == null || !firstPos.getPosBack().equals(tile.pos)); //can't be added to begining of set
        if (canBeAddedToEnd || canBeAddedToStart) return true;
        else return false;
    }

    private boolean addToRun(TileDomainObject tile){
        if (this.tiles.size() == Rules.highestNumber) return false; //is inevitably a duplicate position
        PosDomainObject lastPos = this.tiles.getLast().pos; 
        boolean canBeAddedToEnd = !(lastPos.getPosAhead() == null || !lastPos.getPosAhead().equals(tile.pos)); //if wrap is not enabled tile can't be added after last Pos, and tile must follow from the last one both in color and position
        PosDomainObject firstPos = this.tiles.getFirst().pos;
        boolean canBeAddedToStart = !(firstPos.getPosBack() == null || !firstPos.getPosBack().equals(tile.pos)); //can't be added to begining of set
        if (canBeAddedToEnd) this.tiles.addLast(tile);
        else if (canBeAddedToStart) this.tiles.addFirst(tile);
        else return false;

        addTileSetToTile(tile);
        return true;
    }

    private boolean addToPair(TileDomainObject tile){
        if (this.tiles.getFirst().pos.num != tile.pos.num) return false; //has to be the same number
        for (TileDomainObject t: this.tiles){
            if (t.pos.equals(tile.pos)) return false; //can't be duplicate positions
        }
        this.tiles.add(tile);
        addTileSetToTile(tile);
        return true;
    }

    private boolean validateSet(TileDomainObject... tiles){
        return validateSet(new ArrayList<>(List.of(tiles)));
    }    

    private boolean validateSet(List<TileDomainObject> tiles){
        if (tiles.size() < 3) return false; //a set must be at least three tiles long
        this.tiles = new ArrayList<>();
        this.setType = (tiles.get(0).pos.color.equals(tiles.get(1).pos.color))? TileSet.RUN: TileSet.PAIR; //determines set type by checking if two tiles have the same color
        tiles.sort((t1, t2) -> t1.pos.num - t2.pos.num); //used for run sets
        TileDomainObject firstTile = tiles.removeFirst(); //use the first tile as the basis for adding other tiles 
        addTileSetToTile(firstTile);
        this.tiles.add(firstTile);

        boolean reachedTheEndOfForwardRun = false; //***NEW*** in case of 1, 2, 12, 13. will go from 1-2, and then reverse backward from 13-12

        int index = 0;
        for (int i = 0; i < tiles.size(); i++){
            TileDomainObject tile = tiles.get(index);
            if (!this.add(tile)){
                if (!reachedTheEndOfForwardRun){ //assumes the tile is 13, and now the list has to be reversed **see 4 lines above
                    reachedTheEndOfForwardRun = true;
                    index = tiles.size(); //start calculating backwards
                    i--; //the tile may be tile 12 in example above, and will be calculated later
                }
                else {
                    this.breakupSet();
                    return false;
                }                
            }
            index += (reachedTheEndOfForwardRun)? -1: 1;
        }
        return true;
    }   

    private void addTileSetToTile(TileDomainObject tile){
        tile.set = this;
    }

    private void removeTileSetFromTile(TileDomainObject tile){
        tile.set = null;
    }

    public ArrayList<TileDomainObject> getAllTilesInSet(){
        ArrayList<TileDomainObject> tiles = new ArrayList<>();
        for (TileDomainObject t: this.tiles){
            tiles.add(t);
        }
        return tiles;
    }

    @Override
    public String toString(){
        String s = "{";
        for (TileDomainObject tile: this.tiles){
            s += tile.toString() + " ";
        }
        return s + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(tiles);
    }

    @Override
    public TileSet clone(){
        ArrayList<TileDomainObject> copiedTiles = new ArrayList<>();
        for (TileDomainObject t: this.tiles){
            copiedTiles.add(t.clone());
        }

        TileSet newSet = new TileSet(copiedTiles);

        return newSet;
    }






//old way of validating set. but can work "staticly"
/*     public static ArrayList<Tile> validateSet(int setType, Tile... tiles){
        return (setType == TileSet.RUN)? validateRun(tiles): validatePair(tiles);
    }

    public static ArrayList<Tile> validateRun(Tile... tiles){
        if (tiles.length < 3 || (tiles.length > Rules.highestNumber)) return null; //to prevent duplicates if wrap around is enabled
        ArrayList<Tile> sortedTiles = (ArrayList<Tile>)List.of(tiles);
        sortedTiles.sort((t1, t2) -> t1.pos.num - t2.pos.num); //sort tiles in ascending order
        return (Rules.enableWrapAround)? validateRunWithWrap(sortedTiles): validateRunNoWrap(sortedTiles);
    }

    public static ArrayList<Tile> validatePair(Tile... tiles){
        if (tiles.length < 3) return null;
        int num = tiles[0].pos.num;
        HashSet<Pos> ensureNoDuplicates = new HashSet<>();
        for (Tile tile: tiles){
            //must all be the same number, and no duplicate positions
            if (tile.pos.num != num || !ensureNoDuplicates.add(tile.pos)) return null;
        }
        return (ArrayList<Tile>)List.of(tiles);
    }

    private static ArrayList<Tile> validateRunNoWrap(ArrayList<Tile> tiles){
        Tile tile = tiles.getFirst();
        Color color = tile.pos.color;
        for (int i = 1; i < tiles.size(); i++){
            Pos nextPos = tile.pos.getPosAhead();
            Tile nextTile = tiles.get(i);
            if (nextPos == null) return null; //somes tiles are not between the lowest tile and the upper boundry
            if (!nextPos.equals(nextTile.pos)) return null;
            tile = nextTile;
        }
        return tiles;
    }

    private static ArrayList<Tile> validateRunWithWrap(ArrayList<Tile> tiles){
        Tile tile = tiles.getFirst();
        Color color = tile.pos.color;
        int i = 1;
            for (; i < tiles.size(); i++){
                Tile nextTile = tiles.get(i);
                if (!color.equals(nextTile.pos.color)) return null;
                if (!tile.pos.getPosAhead().equals(nextTile.pos)) break;
                tile = nextTile;
            }
            if (i == tiles.size()) return tiles;
            for (int j = i; j < tiles.size(); j++){
                Tile nextTile = tiles.get(j);
                if (!tile.pos.getPosAhead().equals(nextTile.pos)) return null;
                tile = nextTile;
            }
            if (!tile.pos.getPosAhead().equals(tiles.getFirst().pos)) return null;
            List<Tile> part1 = tiles.subList(i, tiles.size());
            part1.addAll(tiles.subList(0, i));
            return (ArrayList<Tile>)part1;
    } */
}
