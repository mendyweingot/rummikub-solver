package com.mw.rummi.restServices.responseObjects.rummiCheatAPIResponseObjects;

import java.util.ArrayList;

import com.mw.rummi.restServices.WebTile;

public class ValidateSetsInOrderResponseObject {
    
    private ArrayList<ArrayList<WebTile>> invalidsets;

    public ValidateSetsInOrderResponseObject(ArrayList<ArrayList<WebTile>> invalidsets){
        this.invalidsets = invalidsets;
    }

    public ValidateSetsInOrderResponseObject() {}

    public ArrayList<ArrayList<WebTile>> getInvalidsets(){
        return invalidsets;
    }

    public void setInvalidsets(ArrayList<ArrayList<WebTile>> invalidsets){
        this.invalidsets = invalidsets;
    }
}
