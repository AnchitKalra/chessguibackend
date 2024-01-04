package org.example.controller;


import org.example.model.ChessPieces;
import org.example.model.ChessState;
import org.example.service.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

@Controller
@CrossOrigin(origins = "*")
public class ChessController {


    @Autowired
    ChessService chessService;

    @RequestMapping(method = RequestMethod.GET, value = "/chess/save")
    public void savePieces() {
            System.out.println(chessService.saveChessPieces());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/chess/getChessPieces")
    @ResponseBody
    public ResponseEntity<List<ChessPieces>> retreiveChessPieces() {
        List<ChessPieces> chessPiecesList = chessService.retreiveChessPieces();

        if(chessPiecesList != null) {
            return new ResponseEntity<>(chessPiecesList, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/chess/getState")
    @ResponseBody
    public ResponseEntity<List<ChessState>> saveChessState(@RequestBody(required = true) String state) {
        try {
            System.out.println("****************************************gameId********************************************");
            System.out.println("************state***********");
            System.out.println(state);
            String str [] = state.split("null");
            System.out.println("**************************************str[0]*********************");
            System.out.println(str[0]);

            Map<Integer, Integer> map = new HashMap<>();
            Integer a = 0;
            Integer prev = -20;
            boolean flag = false;
            String gameId = "";
            state = str[0];
            for(int i = 0; i < str[0].length(); i++) {
                try{
               // System.out.println(state.charAt(i));

                if(state.charAt(i) == '[' || state.charAt(i) == ']' || state.charAt(i) == ',' || (state.charAt(i) >= 'a' && state.charAt(i) <= 'z')) {
                    continue;
                }
                else{
                           char b = state.charAt(i);
                           if(b == '-') {
                               flag = true;
                               continue;
                           }

                               a = Integer.parseInt("" + b);

                           int j = i + 1;
                           while(state.charAt(j) >= '0' && state.charAt(j) <= '9') {
                              a *= 10;
                              b = state.charAt(j++);
                              a += Integer.parseInt("" + b);

                                  i = j;

                           }
                           if(flag) {
                               a = -a;
                               flag = false;
                           }
                           System.out.println("A->" + a);
                           System.out.println("PREV-> " + prev);
                           if(!prev.equals(-20)) {
                               map.put(prev, a);
                               prev = -20;
                           }
                           else {
                               prev = a;
                           }


                }
            }catch (Exception e) {
                System.out.println(e);
                }
            }

            state = str[1];
            System.out.println("************************************state str[1]***************************************");
            System.out.println(state);

            for(int i = 0; i < state.length() -2 ; i++) {
                gameId += state.charAt(i);
            }
            List<Integer> idList = new ArrayList<>();
            List<Integer> pieceValueList = new ArrayList<>();
            for(int i = 0; i < 64; i++) {
                idList.add(i);
                pieceValueList.add(map.get(i));
            }
            System.out.println(map);
           List<ChessState> stateList = chessService.saveState(idList, pieceValueList, gameId);
           if(stateList != null) {
               return new ResponseEntity<>(stateList, HttpStatus.OK);
           }


        }catch (Exception e) {
            System.out.println(e);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/chess/retreiveState")
    @ResponseBody
    public ResponseEntity<List<ChessState>>retreiveChessState(String gameId) {

        List<ChessState> list = chessService.retreiveState(gameId);
        if(list != null) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }







//    @RequestMapping(method = RequestMethod.GET, value = "/chess/getState")
//    @ResponseBody
//    public ResponseEntity<List<Integer>> getPieceState() {
//
//    }
}
