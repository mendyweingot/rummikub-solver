package com.mw.rummi.restServices.requestObjects.rummiCheatAPIRequestObjects;

import java.util.ArrayList;

import com.mw.rummi.restServices.WebTile;

public class TilesSolveRequestObject {
    
    private ArrayList<ArrayList<WebTile>> board;

    private ArrayList<WebTile> rack;

    private WebTile tileToGetRidOf;

    public TilesSolveRequestObject() {}

    public TilesSolveRequestObject(ArrayList<ArrayList<WebTile>> board, ArrayList<WebTile> rack, WebTile tileToGetRidOf){
        this.board = board;
        this.rack = rack;
        this.tileToGetRidOf = tileToGetRidOf;
    }

    public ArrayList<ArrayList<WebTile>> getBoard(){
        return this.board;
    }
    public ArrayList<WebTile> getRack(){
        return this.rack;
    }
    public WebTile getTileToGetRidOf(){
        return this.tileToGetRidOf;
    }

    public void setBoard(ArrayList<ArrayList<WebTile>> board){
        this.board = board;
    }
    public void setRack(ArrayList<WebTile> rack){
        this.rack = rack;
    }
    public void setTileToGetRidOf(WebTile tileToGetRidOf){
        this.tileToGetRidOf = tileToGetRidOf;
    }    
}
