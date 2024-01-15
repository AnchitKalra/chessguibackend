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


    List<ChessState> l = new ArrayList<>();
    @Autowired
    ChessRepository chessRepository;
   static String player1GameId = "";

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


    public synchronized List<ChessState> saveState(List<Integer> idList, List<Integer> pieceValueList, String gameId, List<WebSocketSession> sessions) {
        int i = 0;
        List<ChessState> chessStateList = new ArrayList<>();
        if(gameId.equals("")&& pieceValueList != null) {
            UUID uuid = UUID.randomUUID();
            gameId = uuid.toString();
            player1GameId = gameId;
            SocketConnectionHandler.sessionList.add(gameId);

            for (int pieceValue : pieceValueList) {
                if(chessRepository.saveAndGetState(i++, pieceValue, gameId, sessions)) {

                }
                else{
                    break;
                }
            }
        }
        else{


            if(!gameId.equals("")) {
                if ((pieceValueList != null)) {
                if (chessRepository.deleteState(gameId)) {

                        for (int pieceValue : pieceValueList) {
                            System.out.println("FROM CHESS SERVICE " + " PIECEVALUELIST " + pieceValueList);
                            if (chessRepository.saveAndGetState(i++, pieceValue, gameId,sessions)) {
                                System.out.println("I");
                                System.out.println(i);
                                System.out.println(pieceValue);
                                System.out.println(gameId);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            else {
                gameId = player1GameId;
                SocketConnectionHandler.getSessionList().add(gameId);
                System.out.println("GAMEID----" + gameId);
            }
        }
        chessStateList = chessRepository.getChessState(gameId);
        if(chessStateList.size() == 64) {
            if(!sessions.isEmpty() && SocketConnectionHandler.getSessionList().size() > 1) {
             System.out.println(   SocketConnectionHandler.sessionList.size());
                if(chessStateList.get(63).getPlayer2() == null) {

                        for (i = 0; i <= 63; i++) {
                            chessStateList.get(i).setPlayer2("player2");
                        }


                }
            }
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
