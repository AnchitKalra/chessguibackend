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

    static List<WebSocketSession> webSocketSessions = new ArrayList<>();


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
      webSocketSessions  = SocketConnectionHandler.getWebSocketSessions();



        try{


            if((SocketConnectionHandler.getSessionList().isEmpty() || SocketConnectionHandler.getSessionList().size() % 2 == 0) && (webSocketSessions.isEmpty() || webSocketSessions.size()  % 2 == 0)) {
                System.out.println("websocket here!");


                String str[] = state.split("null");
                System.out.println(str[0]);


                Map<Integer, Integer> map = new HashMap<>();
                Integer a = 0;
                Integer prev = -20;
                boolean flag = false;
                String gameId = "";
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






                try {
                    if (!SocketConnectionHandler.getSessionList().isEmpty()) {
                        List<WebSocketSession> list = SocketConnectionHandler.getWebSocketSessions();
                        Map<String, String> map1 = SocketConnectionHandler.getSessionList();
                        gameId = map1.get(list.get(list.size() -2).getId());

                    }
                }catch (Exception e) {
                    System.out.println(e);
                    System.out.println("exception from gameId");
                }

                List<ChessState> stateList = chessService.saveState(idList, pieceValueList, gameId, webSocketSessions);
                if (stateList != null) {

                    return new ResponseEntity<>(stateList, HttpStatus.OK);
                }
            }
            else {
                System.out.println("else");


                List<ChessState> stateList = chessService.saveState(null, null, "", webSocketSessions);
                List<ChessState> l = new ArrayList<>();
                int j = 0;
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < 64; i++) {
                    list.add(stateList.get(i).getId());
                   // System.out.println(list);
                }
                Collections.sort(list);
                for (int i = 63; i >= 0; i--) {

                    for (int k = 0; k < 64; k++) {
                        if(stateList.get(k).getId().equals(list.get(i))) {
                            ChessState state1 = stateList.get(k);
                            state1.setBoardValue(j++);
                            l.add(state1);

                            break;
                        }
                    }
                }



                return new ResponseEntity<>(l, HttpStatus.OK);
            }

        }catch (Exception e) {
            System.out.println(e);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/chess/retrieveState")
    @ResponseBody
    public ResponseEntity<List<ChessState>>retreiveChessState() {


        List<ChessState> list = chessService.retreiveState("");
        List<ChessState> l = new ArrayList<>();

        if(list != null) {



                List<ChessState> l1 = new ArrayList<>();
                int j = 0;
                List<Integer> list1 = new ArrayList<>();
                for (int i = 0; i < 64; i++) {
                    list1.add(list.get(i).getId());
                    // System.out.println(list);
                }
                Collections.sort(list1);
                for(int i = 63; i >= 0; i--) {
                    for (int k = 0; k < 64; k++) {
                        if (list1.get(i).equals(list.get(k).getId())) {
                            ChessState state = list.get(k);
                            state.setBoardValue(j++);
                            l1.add(state);
                            break;
                        }
                    }
                }


                return new ResponseEntity<>(l1, HttpStatus.OK);

        }

        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }







//    @RequestMapping(method = RequestMethod.GET, value = "/chess/getState")
//    @ResponseBody
//    public ResponseEntity<List<Integer>> getPieceState() {
//
//    }
}
