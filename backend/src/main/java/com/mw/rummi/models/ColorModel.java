package com.mw.rummi.models;

import com.mw.rummi.Rules;
import com.mw.rummi.domainObjects.ColorDomainObject;

import java.util.HashSet;
import java.util.List;

public class ColorModel {
    
    public static final ColorDomainObject RED = new ColorDomainObject(0);
    public static final ColorDomainObject GREEN = new ColorDomainObject(1);
    public static final ColorDomainObject BLUE = new ColorDomainObject(2);
    public static final ColorDomainObject BLACK = new ColorDomainObject(3);

    public static ColorDomainObject createColor(int value){
        if (!validate(value)) return null;

        switch (value) {
            case 0: return ColorModel.RED ;
            case 1: return ColorModel.GREEN ;
            case 2: return ColorModel.BLUE ;
            default: return ColorModel.BLACK ;
        }
    }
    
    public static HashSet<ColorDomainObject> getAllColors(){
        HashSet<ColorDomainObject> colors = new HashSet<>(List.of(ColorModel.RED, ColorModel.GREEN, ColorModel.BLUE, ColorModel.BLACK));
        return colors;//(HashSet<Color>)Set();
    }

    private static boolean validate(int value){
        return value >= 0 && value < Rules.numberOfColors;
    }
}
