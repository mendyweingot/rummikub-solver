package com.mw.rummi.restServices.requestObjects.rummiCheatAPIRequestObjects;

import java.util.ArrayList;

import com.mw.rummi.restServices.WebTile;

public class ValidateSetsInOrderRequestObject {
    
    private ArrayList<ArrayList<WebTile>> board;

    public ValidateSetsInOrderRequestObject() {}

    public ValidateSetsInOrderRequestObject(ArrayList<ArrayList<WebTile>> board){
        this.board = board;
    }

    public ArrayList<ArrayList<WebTile>> getBoard(){
        return this.board;
    }

    public void setBoard(ArrayList<ArrayList<WebTile>> board){
        this.board = board;
    }
    
}
