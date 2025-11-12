package com.mw.rummi.models;

import java.util.HashSet;

import com.mw.rummi.Rules;
import com.mw.rummi.domainObjects.ColorDomainObject;
import com.mw.rummi.domainObjects.PosDomainObject;

public class PosModel {

    public static PosDomainObject createPos(int num, int color){
        if (!validatePos(num)) return null;
        ColorDomainObject c = ColorModel.createColor(color);
        if (c == null) return null;
        return new PosDomainObject(num, c);
    }

    private static int getNextNum(int num){
        return (num != Rules.highestNumber)? num + 1: (Rules.enableWrapAround)? 1: -1;
    }

    private static int getPreviousNum(int num){
        return (num != 1)? num -1: (Rules.enableWrapAround)? Rules.highestNumber: -1;
    }

    public static PosDomainObject getPosAhead(PosDomainObject pos){
        int num = pos.num;
        if (getNextNum(num) == -1) return null;
        return new PosDomainObject(getNextNum(num), pos.color);
    }

    public static PosDomainObject getPosBack(PosDomainObject pos){
        int num = pos.num;
        if (getPreviousNum(num) == -1) return null;
        return new PosDomainObject(getPreviousNum(num), pos.color);
    }

    public static HashSet<PosDomainObject> getPosPairs(PosDomainObject pos){
        HashSet<PosDomainObject> positions = new HashSet<>();
        for (ColorDomainObject color: ColorModel.getAllColors()){
            if (pos.color.equals(color)) continue;
            positions.add(new PosDomainObject(pos.num, color));
        }
        return positions;
    }

    private static boolean validatePos(int num){
        return num > 0 && num <= Rules.highestNumber;
    }
}
