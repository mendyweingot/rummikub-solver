package com.mw.rummi.models;

import com.mw.rummi.Rules;
import com.mw.rummi.domainObjects.ColorDomainObject;
import com.mw.rummi.domainObjects.PosDomainObject;
import com.mw.rummi.domainObjects.TileDomainObject;
import com.mw.rummi.restServices.*;

public class TileModel {

    public static TileDomainObject createTile(int number, int color, int duplicateNum){
        if (!validate(duplicateNum)) return null;
        PosDomainObject pos = PosModel.createPos(number, color);
        if (pos == null) return null;
        return new TileDomainObject(pos, duplicateNum);
    }

    public static TileDomainObject createTile(WebTile wTile){
        return TileModel.createTile(wTile.getNumber(), wTile.getColor(), wTile.getDupNum());
    }

    public static double getTotal(TileDomainObject tile){
        double total = tile.front + tile.back;
        total += ((tile.mid - total) > 0)? tile.mid - total: 0; //any full set mid has above front+back is considered a new set option
        total += (tile.mid != 0 && tile.front + tile.back != 0)? 0.5: 0; //if mid exists only together with front or back, only 0.5 is added
        boolean containsDecimal = total % 1 != 0.0 || tile.pair % 1 != 0.0; //only one 0.5 will be added
        total = Math.floor(total) + Math.floor(tile.pair) + ((containsDecimal)? 0.5: 0);
        return total;
    }

    private static boolean validate(int duplicateNum){
        return duplicateNum > 0 && duplicateNum <= Rules.numberOfDuplicates;
    }
    
}
