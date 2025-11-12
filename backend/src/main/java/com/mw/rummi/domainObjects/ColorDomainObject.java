package com.mw.rummi.domainObjects;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class ColorDomainObject {

    private final int value;
    public ColorDomainObject(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof ColorDomainObject)) return false;
        ColorDomainObject c = (ColorDomainObject)o;
        return this.getValue() == c.getValue();
    }

    @Override
    public String toString(){
        switch (value){
            case 0: return "RED";
            case 1: return "GREEN";
            case 2: return "BLUE";
            case 3: return "BLACK";
            default: return "OOPS";
        }
    }

    @Override
    public int hashCode(){
        return value;
    }
}
