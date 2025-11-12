package com.mw.rummi.restServices.responseObjects.rummiCheatAPIResponseObjects;

import java.util.ArrayList;

import com.mw.rummi.restServices.WebTile;

public class TileSolveResponseObject {

    ArrayList<ArrayList<WebTile>> newSets;

    public TileSolveResponseObject(){}

    public TileSolveResponseObject(ArrayList<ArrayList<WebTile>> newSets){
        this.newSets = newSets;
    }

    public ArrayList<ArrayList<WebTile>> getNewSets(){
        return this.newSets;
    }

    public void newSetSteps(ArrayList<ArrayList<WebTile>> newSets){
        this.newSets = newSets;
    }




    public static class Step {
        public WebTile tileToGetRidOf;
        public ArrayList<WebTile> setOfTile;

        public Step() {}
        public Step(WebTile tileToGetRidOf, ArrayList<WebTile> setOfTile){
            this.tileToGetRidOf = tileToGetRidOf;
            this.setOfTile = setOfTile;
        }

        public WebTile getTileToGetRidOf(){
            return this.tileToGetRidOf;
        }
        public ArrayList<WebTile> getSetOfTile(){
            return this.setOfTile;
        }

        public void setTileToGetRidOf(WebTile tileToGetRidOf){
            this.tileToGetRidOf = tileToGetRidOf;
        }
        public void setSetOfTile(ArrayList<WebTile> setOfTile){
            this.setOfTile = setOfTile;
        }

    }
    
}
