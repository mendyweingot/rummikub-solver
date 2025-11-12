package com.mw.rummi.restServices;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mw.rummi.controller.TileSolveAlgorithm;
import com.mw.rummi.domainObjects.ColorDomainObject;
import com.mw.rummi.domainObjects.TileDomainObject;
import com.mw.rummi.domainObjects.TilePile;
import com.mw.rummi.domainObjects.TileSet;
import com.mw.rummi.models.ColorModel;
import com.mw.rummi.models.TileModel;
import com.mw.rummi.models.TileSetModel;
import com.mw.rummi.restServices.requestObjects.rummiCheatAPIRequestObjects.TilesSolveRequestObject;
import com.mw.rummi.restServices.requestObjects.rummiCheatAPIRequestObjects.ValidateSetsInOrderRequestObject;
import com.mw.rummi.restServices.responseObjects.rummiCheatAPIResponseObjects.TileSolveResponseObject;
import com.mw.rummi.restServices.responseObjects.rummiCheatAPIResponseObjects.ValidateSetsInOrderResponseObject;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class RummiCheatAPIController {

    @PostMapping("/tilesolver")
    public TileSolveResponseObject tileSolve(@RequestBody TilesSolveRequestObject request) throws Exception {

        ArrayList<ArrayList<WebTile>> wBoard = request.getBoard();
        ArrayList<WebTile> wRack = request.getRack();
        WebTile wTileToGetRidOf = request.getTileToGetRidOf();

        ArrayList<TileSet> boardSets = new ArrayList<>();
        TilePile board = new TilePile();
        TilePile rack = new TilePile();

        for (ArrayList<WebTile> wSet: wBoard){
            ArrayList<TileDomainObject> setTiles = new ArrayList<>();
            for (WebTile wTile: wSet){
                TileDomainObject tile = TileModel.createTile(wTile);
                board.addTile(tile);
                setTiles.add(tile);
            }
            TileSet set = new TileSet(setTiles);
            boardSets.add(set);
        }

        for (WebTile wTile: wRack){
            TileDomainObject tile = TileModel.createTile(wTile);
            rack.addTile(tile);
        }

        TileDomainObject tileToGetRidOf = TileModel.createTile(wTileToGetRidOf);
        rack.remove(tileToGetRidOf);

        //solve the tile placement
        TileSolveAlgorithm alg = new TileSolveAlgorithm(board, rack, boardSets);
        TileSolveAlgorithm.Process steps = alg.findPlaceForTile(tileToGetRidOf);

        HashSet<TileSet> affectedTilesSets = new HashSet<>();
        for (TileSolveAlgorithm.Process.StepInProcess step: steps.steps){
            affectedTilesSets.add(step.tileToGetRidOf.set);
        }

        ArrayList<ArrayList<WebTile>> newSets = new ArrayList<>();

        for (TileSet set: affectedTilesSets){
            ArrayList<WebTile> newSet = new ArrayList<>();
            for (TileDomainObject setTile: set.getAllTilesInSet()){
                newSet.add(new WebTile(setTile));
            }
            newSets.add(newSet);
        }


        return new TileSolveResponseObject(newSets);
    }

    @PostMapping("/validatesetsinorder")
    public ValidateSetsInOrderResponseObject validateSetsInOrder(@RequestBody ValidateSetsInOrderRequestObject request){

        ArrayList<ArrayList<TileDomainObject>> invalidsets = new ArrayList<>();
        
        ArrayList<ArrayList<WebTile>> wInvalidsets = new ArrayList<>();

        for (ArrayList<WebTile> wSet: request.getBoard()){
            
            ArrayList<WebTile> wInvalidSet = new ArrayList<>();
            boolean isSetInvalidByInvalidTile = false; //one of the tiles of the set is invalid itself, then the rest of the set can't be valid
            ArrayList<TileDomainObject> set = new ArrayList<>();

            for (WebTile wTile: wSet){
                TileDomainObject tile = TileModel.createTile(wTile);
                if (tile == null) {
                    isSetInvalidByInvalidTile = true;

                    for (TileDomainObject t: set){
                        wInvalidSet.add(new WebTile(t));
                    }
                    set.clear();
                }
                if (isSetInvalidByInvalidTile){
                    wInvalidSet.add(wTile);
                }
                else {
                    set.add(tile);
                }
            }

            if (isSetInvalidByInvalidTile){
                wInvalidsets.add(wInvalidSet);
            }

            if (!TileSetModel.isValidSetInOrder(set)){
                invalidsets.add(set);
            }
        }


        for (ArrayList<TileDomainObject> set: invalidsets){
            ArrayList<WebTile> wSet = new ArrayList<>();
            for (TileDomainObject tile: set){
                wSet.add(new WebTile(tile));
            }
            wInvalidsets.add(wSet);
        }

        return new ValidateSetsInOrderResponseObject(wInvalidsets);
    }
}
