package com.mw.rummi.domainObjects;
import java.util.HashSet;

import com.mw.rummi.Rules;
import com.mw.rummi.models.ColorModel;

// default is highest number = 13, and numbers wrap around
public class PosDomainObject {
    public final int num;
    public final ColorDomainObject color;

    public PosDomainObject(int num, ColorDomainObject color){
        this.num = num;
        this.color = color;
    }

    // start model
    private int getNextNum(){
        return (num != Rules.highestNumber)? num + 1: (Rules.enableWrapAround)? 1: -1;
    }

    private int getPreviousNum(){
        return (num != 1)? num -1: (Rules.enableWrapAround)? Rules.highestNumber: -1;
    }

    public PosDomainObject getPosAhead(){
        if (this.getNextNum() == -1) return null;
        return new PosDomainObject(this.getNextNum(), this.color);
    }

    public PosDomainObject getPosBack(){
        if (this.getPreviousNum() == -1) return null;
        return new PosDomainObject(this.getPreviousNum(), this.color);
    }

    public HashSet<PosDomainObject> getPosPairs(){
        HashSet<PosDomainObject> positions = new HashSet<>();
        for (ColorDomainObject color: ColorModel.getAllColors()){
            if (this.color.equals(color)) continue;
            positions.add(new PosDomainObject(this.num, color));
        }
        return positions;
    }
    //end model

    @Override
    public boolean equals(Object o){
        if (!(o instanceof PosDomainObject)) return false;
        PosDomainObject p = (PosDomainObject)o;
        return this.num == p.num && this.color.equals(p.color);
    }

    @Override
    public String toString(){
        return num + "-" + color.toString();
    }

    @Override
    public int hashCode(){
        return java.util.Objects.hash(num, color.hashCode());
    }
}
