package com.mw.rummi.models;

import java.util.ArrayList;

import com.mw.rummi.Rules;
import com.mw.rummi.domainObjects.PosDomainObject;
import com.mw.rummi.domainObjects.TileDomainObject;
import com.mw.rummi.domainObjects.TileSet;

public class TileSetModel {

    public static boolean isValidSetInOrder(ArrayList<TileDomainObject> t){
        if (t.size() < 3) return false; //set is too small

        ArrayList<TileDomainObject> tiles = new ArrayList<>();
        for (TileDomainObject tile: t){
            tiles.add(tile);
        }

        ArrayList<TileDomainObject> setTiles = new ArrayList<>();

        boolean isRun = tiles.get(0).pos.color.equals(tiles.get(1).pos.color); //determines set type by checking if two tiles have the same color

        TileDomainObject firstTile = tiles.removeFirst();
        setTiles.add(firstTile);

        if (!isRun){
            for (TileDomainObject tile: tiles){
                if (!TileSetModel.addToPair(tile, setTiles)) return false;
            }
        }
        else {
            for (TileDomainObject tile: tiles){
                if (setTiles.size() == Rules.highestNumber) return false; //is inevitably a duplicate position
                PosDomainObject lastPos = setTiles.getLast().pos; 
                if (!(lastPos.getPosAhead() == null || !lastPos.getPosAhead().equals(tile.pos))){ //if wrap is not enabled tile can't be added after last Pos, and tile must follow from the last one both in color and position
                    setTiles.addLast(tile);
                }
                else {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean addToPair(TileDomainObject tile, ArrayList<TileDomainObject> setTiles){
        if (setTiles.getFirst().pos.num != tile.pos.num) return false; //has to be the same number
        for (TileDomainObject t: setTiles){
            if (t.pos.equals(tile.pos)) return false; //can't be duplicate positions
        }
        setTiles.add(tile);
        return true;
    }
}
