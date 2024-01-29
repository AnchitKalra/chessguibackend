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
        System.out.println(state);
        StringBuilder gameId = new StringBuilder();








        try{
            webSocketSessions = SocketConnectionHandler.getWebSocketSessions();

            String str[] = state.split("null");
            try {

                if (str[1].length() > 4) {
                    for (int j = 0; j < str[1].length() - 2;j++) {

                        if (str[1].charAt(j) == '[' || str[1].charAt(j) == ']' || str[1].charAt(j) == ',' || str[1].charAt(j) == '=') {
                            continue;
                        }

                        gameId.append(str[1].charAt(j));
                        System.out.println(gameId + "   from str1" + "  " + str[1]);

                    }


                }
                else {
                    gameId = new StringBuilder();
                    System.out.println(gameId);
                }
            }catch (Exception e) {
                System.out.println(e);
                System.out.println("exception from gameId");
            }



            if(!gameId.toString().equals("") || webSocketSessions.isEmpty() || (webSocketSessions.size() % 2 == 0 && (SocketConnectionHandler.getSessionList().isEmpty() || SocketConnectionHandler.getSessionList().size() % 2 == 0))){
                System.out.println("websocket here!");
                System.out.println(state);


                System.out.println(str[0]);


                Map<Integer, Integer> map = new HashMap<>();
                int a;
                Integer prev = -20;
                boolean flag = false;
                state = str[0];
                for (int i = 0; i < str[0].length() - 1; i++) {
                    try {
                        // System.out.println(state.charAt(i));

                        if (state.charAt(i) == '[' || state.charAt(i) == ']' || state.charAt(i) == ',' || (state.charAt(i) >= 'a' && state.charAt(i) <= 'z')) {
                            continue;
                        } else {
                            char b = state.charAt(i);
                            if (b == '-') {
                                flag = true;
                                continue;
                            }

                            a = Integer.parseInt("" + b);

                            int j = i + 1;
                            while (state.charAt(j) >= '0' && state.charAt(j) <= '9') {
                                a *= 10;
                                b = state.charAt(j++);
                                a += Integer.parseInt("" + b);

                                i = j;

                            }
                            if (flag) {
                                a = -a;
                                flag = false;
                            }

                            if (!prev.equals(-20)) {
                                map.put(prev, a);
                                prev = -20;
                            } else {
                                prev = a;
                            }


                        }
                    } catch (Exception e) {
                        System.out.println(e);
                        System.out.println("from str0.length");
                    }
                }

                List<Integer> idList = new ArrayList<>();
                List<Integer> pieceValueList = new ArrayList<>();
                try {

                    for (int i = 0; i < 64; i++) {
                        idList.add(i);
                        if (map.get(i) != null) {
                            pieceValueList.add(map.get(i));
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println("from piece value");
                }







                List<ChessState> stateList = chessService.saveState(idList, pieceValueList, gameId.toString(), webSocketSessions);
                if (stateList != null) {
                   Collections.sort(stateList);
                    return new ResponseEntity<>(stateList, HttpStatus.OK);
                }
            }
            else {
                System.out.println("else");


                List<ChessState> stateList = chessService.saveState(null, null, "", webSocketSessions);

                if(!stateList.isEmpty()) {
                   Collections.sort(stateList);


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
    public ResponseEntity<List<ChessState>>retreiveChessState(@RequestBody(required = false) String s) {

        StringBuilder gameId = new StringBuilder();
        System.out.println(s);

        try {


                for (int j = 0; j < s.length() - 1;j++) {

                    if (s.charAt(j) == '[' || s.charAt(j) == ']' || s.charAt(j) == ',' || s.charAt(j) == '=') {
                        continue;
                    }

                    gameId.append(s.charAt(j));

                }



        }catch (Exception e) {
            System.out.println(e);
            System.out.println("exception from gameId retreiveState");
        }



        List<ChessState> list = chessService.retreiveState(gameId.toString());

                if(!list.isEmpty()) {
                    Collections.sort(list);
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
            int turn = 0;
            try {
                turn = Integer.parseInt(s[2]);
            }catch (Exception e) {
                System.out.println(e);
            }

            List<ChessState> l = chessService.getState(gameId, index, turn);
            Collections.sort(l);
            return new ResponseEntity<>(l, HttpStatus.OK);
        }catch (Exception e) {
            System.out.println(e);
        }

        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }










}
