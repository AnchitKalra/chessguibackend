package org.example.controller;

import org.example.config.SocketConnectionHandler;
import org.example.model.ChessPieces;
import org.example.model.ChessState;
import org.example.service.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;
import java.util.*;

@Controller
@CrossOrigin(origins = "*")
public class ChessController {






    @Autowired
    ChessService chessService;

    static List<SocketConnectionHandler> socketConnectionHandlerList = new ArrayList<>();

     List<WebSocketSession> webSocketSessions = new ArrayList<>();
     @RequestMapping("/")
     public String hello() {
       return "index";
       }


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
    public ResponseEntity<List<ChessState>> saveChessState(@RequestBody(required = true) String state[]) {
        System.out.println("from saveChessState");


        String gameId = "";
        try{
            if(state.length == 130) {
                gameId = state[129];
            }
        }
        catch (Exception e) {
            System.out.println("from gameid");
            System.out.println(e);
        }








        try{
            webSocketSessions = SocketConnectionHandler.getWebSocketSessions();

            if(!gameId.equals("") || webSocketSessions.isEmpty() || (webSocketSessions.size() % 2 == 0 && (SocketConnectionHandler.getSessionList().isEmpty() || SocketConnectionHandler.getSessionList().size() % 2 == 0))) {

                List<Integer> idList = new ArrayList<>();
                List<Integer> pieceValueList = new ArrayList<>();
                for (int i = 0; i < 128; i+=2) {
                    idList.add(Integer.parseInt(state[i]));
                    Integer a = Integer.parseInt(state[i + 1]);
                    pieceValueList.add(a);
                }

                int turn = Integer.parseInt(state[128]);



                List<ChessState> stateList = chessService.saveState(idList, pieceValueList, gameId, webSocketSessions, turn);
                if (stateList != null) {
                    return new ResponseEntity<>(stateList, HttpStatus.OK);
                }
            }



            else {

                int turn = 2;


                List<ChessState> stateList = chessService.saveState(null, null, "", webSocketSessions, turn);

                if(!stateList.isEmpty()) {



                    return new ResponseEntity<>(stateList, HttpStatus.OK);
                }
            }

        }catch (Exception e) {

            System.out.println(e);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }





    @RequestMapping(method = RequestMethod.POST, value = "/chess/retrieveState")
    @ResponseBody
    public ResponseEntity<List<ChessState>>retreiveChessState(@RequestBody(required = false) String s[]) {
        System.out.println("from retrieveState");

        String gameId = "";
        System.out.println(s[0]);
        System.out.println(s[1]);
        int turn = 1;

        try {
            gameId = s[0];
            turn = Integer.parseInt(s[1]);



        }catch (Exception e) {
            System.out.println(e);
        }



        List<ChessState> list = chessService.retreiveState(gameId, turn);

                if(!list.isEmpty()) {

                    return new ResponseEntity<>(list, HttpStatus.OK);
                }



        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/chess/previous")
    @ResponseBody
    public ResponseEntity<List<ChessState>> getPreviousState(@RequestBody(required = true) String s[]) {
        try {
            String gameId = s[0];
            System.out.println(s[0]);
            int index = Integer.parseInt(s[1]);
            int player = 0;
            int turn = 0;
            try {
                player = Integer.parseInt(s[2]);
                turn = Integer.parseInt(s[3]);

            }catch (Exception e) {
                System.out.println(e);
            }

            List<ChessState> l = chessService.getState(gameId, index, turn);
            if(index >= 0 && turn == player) {
                Collections.sort(l, Collections.reverseOrder());
            }
            else {
                Collections.sort(l);
            }
            return new ResponseEntity<>(l, HttpStatus.OK);
        }catch (Exception e) {
            System.out.println(e);
        }

        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/chess/close")
    public void closeConnection() {
        SocketConnectionHandler.removeIdleSessions();
    }










}
//GOOGLE_RUNTIME_VERSION