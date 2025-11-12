package com.mw.rummi.restServices;

import com.mw.rummi.domainObjects.TileDomainObject;

public class WebTile {
    private int number;
    private int color;
    private int dupNum;
    private boolean joker;

    public WebTile(int n, int c, int d, boolean j){
        number = n;
        color = c;
        dupNum = d;
        joker = j;
    }

    public WebTile(TileDomainObject tile){
        this.number = tile.pos.num;
        this.color = tile.pos.color.getValue();
        this.dupNum = tile.duplicateNum;
        this.joker = tile.joker;
    }

    public WebTile() {}

    public int getNumber(){
        return number;
    }
    public void setNumber(int number){
        this.number = number;
    }

    public int getColor(){
        return color;
    }
    public void setColor(int color){
        this.color = color;
    }

    public int getDupNum(){
        return dupNum;
    }
    public void setDupNum(int dupNum){
        this.dupNum = dupNum;
    }

    public boolean getJoker(){
        return joker;
    }
    public void setJoker(boolean joker){
        this.joker = joker;
    }

}
