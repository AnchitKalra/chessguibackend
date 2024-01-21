package org.example.service;


import org.example.config.SocketConnectionHandler;
import org.example.dao.ChessRepository;
import org.example.model.ChessPieces;
import org.example.model.ChessState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public  class ChessService {


    private String playerGameId = "";
    List<ChessState> l = new ArrayList<>();
    @Autowired
    ChessRepository chessRepository;
    public static String convertUploadedFileToBase64(byte bytes[])  {
        return Base64.getEncoder().encodeToString(bytes);
    }
    public boolean saveChessPieces() {
        try {
            int i = 0;
            List<String> names = new ArrayList<>();
            names.add("blackBishop");
            names.add("whiteBishop");
            names.add("blackPawn");
            names.add("blackKing");
            names.add("whiteKing");
            names.add("blackKnight");
            names.add("whiteKnight");
            names.add("whitePawn");
            names.add("blackQueen");
            names.add("whiteQueen");
            names.add("blackRook");
            names.add("whiteRook");
            for(i = 0; i <= 11; i++) {
                ChessPieces chess = new ChessPieces();
                String path = "src/main/resources/" + i + ".svg";
                byte[] bytes = Files.readAllBytes(Paths.get(path));
                String text = convertUploadedFileToBase64(bytes);
                String s = "data:image/svg;base64," + text;
                chess.setImage(s);
                chess.setPieceName(names.get(i));
                chessRepository.savePieces(chess);
            }

            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
    public List<ChessPieces> retreiveChessPieces() {
        List<ChessPieces> chessPiecesList =  chessRepository.getChessPieces();

        if(chessPiecesList != null) {
            return chessPiecesList;
        }
        return null;
    }


    public  List<ChessState> saveState(List<Integer> idList, List<Integer> pieceValueList, String gameId, List<WebSocketSession> sessions) {
        int i = 0;
        List<ChessState> chessStateList = new ArrayList<>();
        if(gameId.equals("")&& pieceValueList != null) {
            UUID uuid = UUID.randomUUID();
            gameId = uuid.toString();
            playerGameId = gameId;
            for (int pieceValue : pieceValueList) {
                if (!chessRepository.saveAndGetState(i++, pieceValue, gameId, sessions)) {
                   break;
                }
                List<WebSocketSession> list = SocketConnectionHandler.getWebSocketSessions();


                HashMap<String, String > map = SocketConnectionHandler.getSessionList();
                if(list.size() > map.size()) {
                    SocketConnectionHandler.getSessionList().remove("player2");
                    map.put(list.get(list.size() - 1).getId(), gameId);
                    SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), gameId);
                }

            }

        }








        else{


            if(!gameId.equals("")) {
                if ((pieceValueList != null)) {
                if (chessRepository.deleteState(gameId)) {
                    System.out.println("delete");

                        for (int pieceValue : pieceValueList) {

                            if (!chessRepository.saveAndGetState(i++, pieceValue, gameId, sessions)) {
                                System.out.println("not saved");
                               break;
                            }

                            List<WebSocketSession> list = SocketConnectionHandler.getWebSocketSessions();


                            HashMap<String, String > map = SocketConnectionHandler.getSessionList();
                            SocketConnectionHandler.getSessionList().remove("player2");
                            if(list.size() > map.size()) {
                                SocketConnectionHandler.getSessionList().remove("player2");
                                map.put(list.get(list.size() - 1).getId(), gameId);
                                SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), gameId);
                            }





                        }
                    }
                }


            }

        }
        if(gameId.equals("")) {
            HashMap<String, String> map = SocketConnectionHandler.getSessionList();
            List<WebSocketSession> list = SocketConnectionHandler.getWebSocketSessions();
            System.out.println(map.size());
            System.out.println(list.size());

                gameId = map.get(list.get(list.size() - 1).getId());
                System.out.println("gameId" + gameId);
                if(gameId == null) {
                    gameId = playerGameId;
                    SocketConnectionHandler.getSessionList().remove("player2");
                    if(list.size() > map.size()) {
                        SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), playerGameId);
                    }


                }
                if(gameId != (null)) {
                    SocketConnectionHandler.getSessionList().remove("player2");
                    if (list.size() > map.size()) {
                        SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), gameId);
                    }

                }







            chessStateList = chessRepository.getChessState(gameId);
            if (chessStateList.get(63).getPlayer2() == null) {

                for (i = 0; i < 63; i++) {
                    chessStateList.get(i).setPlayer2("player2");
                }
            }
        }
        else {
            chessStateList = chessRepository.getChessState(gameId);
        }



        if(chessStateList.size() == 64) {
            return chessStateList;
        }
        else {
            return null;
        }





    }

    public List<ChessState> retreiveState(String gameId)  {

        List<ChessState> l =  chessRepository.getChessState(gameId);
        return l;
    }












}
