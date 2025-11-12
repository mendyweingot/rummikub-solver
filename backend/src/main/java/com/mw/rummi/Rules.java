package com.mw.rummi;
public class Rules {
    public static boolean enableWrapAround;
    public static int highestNumber;
    public static int numberOfDuplicates;
    public static int numberOfColors;
    public static int numberOfJokers;

    public Rules(){
        defaultRules();
    }

    public static void defaultRules(){
        enableWrapAround = true;
        highestNumber = 13;
        numberOfDuplicates = 2;
        numberOfColors = 4;
        numberOfJokers = 0;
    }
}
