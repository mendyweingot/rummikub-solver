package com.mw.rummi.domainObjects;

public class TileDomainObject {

    public final PosDomainObject pos;
    public final int duplicateNum;

    public final boolean joker;

    public double front;
    public double mid;
    public double back;
    public double pair;

    public TileSet set;

    public TileDomainObject(PosDomainObject pos, int duplicateNum){
        this.pos = pos;
        this.duplicateNum = duplicateNum;
        this.joker = false;
    }

    // start model
    public double getTotal(){
        double total = front + back;
        total += ((mid - total) > 0)? mid - total: 0; //any full set mid has above front+back is considered a new set option
        total += (mid != 0 && front + back != 0)? 0.5: 0; //if mid exists only together with front or back, only 0.5 is added
        boolean containsDecimal = total % 1 != 0.0 || pair % 1 != 0.0; //only one 0.5 will be added
        total = Math.floor(total) + Math.floor(pair) + ((containsDecimal)? 0.5: 0);
        return total;
    }
    // end model

    @Override
    public boolean equals(Object o){
        if (!(o instanceof TileDomainObject)) return false;
        TileDomainObject t = (TileDomainObject)o;
        return this.duplicateNum == t.duplicateNum && this.pos.equals(t.pos);
    }

    @Override
    public String toString(){
        return "(" + pos.toString() + "-" + duplicateNum + ")";
    }

    @Override
    public int hashCode(){
        return java.util.Objects.hash(pos.hashCode(), duplicateNum);
    }

    @Override
    public TileDomainObject clone(){
        return new TileDomainObject(this.pos, this.duplicateNum);
    }
}