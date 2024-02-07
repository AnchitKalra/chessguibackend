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
            int i;
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


    public  List<ChessState> saveState(List<Integer> idList, List<Integer> pieceValueList, String gameId, List<WebSocketSession> sessions, int turn) {
        int i = 0;

        List<ChessState> chessStateList;
        if(gameId.equals("")&& pieceValueList != null) {
            UUID uuid = UUID.randomUUID();
            gameId = uuid.toString();
            playerGameId = gameId;
            for (int pieceValue : pieceValueList) {

                if (!chessRepository.saveAndGetState(idList.get(i++), pieceValue, gameId, sessions)) {
                    System.out.println("save state not saved");
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
            chessStateList = chessRepository.getChessState(gameId);
            Collections.sort(chessStateList);
            List<List<ChessState>> l = new ArrayList<>();
            List<ChessState> l1 = new ArrayList<>();
            for (int j = 1; j <= chessStateList.size(); j++) {
                if(j % 64 == 0) {
                    l1.add(chessStateList.get(j - 1));
                    l.add(l1);
                    l1 = new ArrayList<>();
                }
                else {
                    l1.add(chessStateList.get(j - 1));
                }

            }
            chessStateList = l.get(l.size() - 1);

                Collections.sort(chessStateList);

                return chessStateList;





        }








        else {


            if (!gameId.equals("")) {
                if ((pieceValueList != null)) {
                    i = 0;


                    for (int pieceValue : pieceValueList) {


                        if (!chessRepository.saveAndGetState(idList.get(i++), pieceValue, gameId, sessions)) {
                            System.out.println("not saved");
                            break;
                        }

                        List<WebSocketSession> list = SocketConnectionHandler.getWebSocketSessions();


                        HashMap<String, String> map = SocketConnectionHandler.getSessionList();
                        SocketConnectionHandler.getSessionList().remove("player2");
                        if (list.size() > map.size()) {
                            SocketConnectionHandler.getSessionList().remove("player2");
                            map.put(list.get(list.size() - 1).getId(), gameId);
                            SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), gameId);
                        }


                    }
                }
            } else if (gameId.equals("")) {
                HashMap<String, String> map = SocketConnectionHandler.getSessionList();
                List<WebSocketSession> list = SocketConnectionHandler.getWebSocketSessions();
                System.out.println(map.size());
                System.out.println(list.size());

                gameId = map.get(list.get(list.size() - 1).getId());
                System.out.println("gameId" + gameId);
                if (gameId == null) {
                    gameId = playerGameId;
                    SocketConnectionHandler.getSessionList().remove("player2");
                    if (list.size() > map.size()) {
                        SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), playerGameId);
                    }


                }
                SocketConnectionHandler.getSessionList().remove("player2");
                if (list.size() > map.size()) {
                    SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), gameId);
                }
                chessStateList = chessRepository.getChessState(gameId);
                Collections.sort(chessStateList);
                List<List<ChessState>> l = new ArrayList<>();
                List<ChessState> l1 = new ArrayList<>();
                for (int j = 1; j <= chessStateList.size(); j++) {
                    if(j % 64 == 0) {
                        l1.add(chessStateList.get(j - 1));
                        l.add(l1);
                        l1 = new ArrayList<>();
                    }
                    else {
                        l1.add(chessStateList.get(j - 1));
                    }

                }
                chessStateList = l.get(l.size() - 1);
                if (chessStateList.get(63).getPlayer2() == null) {

                    for (i = 0; i < 64; i++) {
                        System.out.println("player2");
                        chessStateList.get(i).setPlayer2("player2");
                    }
                }
                if(chessStateList.size() == 64) {
                 Collections.sort(chessStateList, Collections.reverseOrder());
                        return chessStateList;
                    }
                }



            chessStateList = chessRepository.getChessState(gameId);
            Collections.sort(chessStateList);
            List<List<ChessState>> l = new ArrayList<>();
            List<ChessState> l1 = new ArrayList<>();
            for (int j = 1; j <= chessStateList.size(); j++) {
                if(j % 64 == 0) {
                    l1.add(chessStateList.get(j - 1));
                    l.add(l1);
                    l1 = new ArrayList<>();
                }
                else {
                    l1.add(chessStateList.get(j - 1));
                }

            }
            chessStateList = l.get(l.size() - 1);
            if (chessStateList.get(63).getPlayer2() == null) {

                for (i = 0; i < 64; i++) {
                    System.out.println("player2");
                    chessStateList.get(i).setPlayer2("player2");
                }
            }


            if(chessStateList.size() == 64) {
                    Collections.sort(chessStateList);

                    return chessStateList;

            }}














            return null;
        }







    public List<ChessState> retreiveState(String gameId, int turn)  {
        try {

            List<WebSocketSession> list = SocketConnectionHandler.getWebSocketSessions();
            Map<String, String> map = SocketConnectionHandler.getSessionList();
            String g = map.get("player2");
            map.remove("player2");
            System.out.println(gameId);
            if (g == null) {
                if(gameId == null || gameId.equals("")) {
                    gameId = playerGameId;
                }
            }
            if (list.size() > map.size()) {
                System.out.println("LIST > MAP");
                map.put(list.get(list.size() - 1).getId(), gameId);
                System.out.println(map);
                SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), gameId);
                SocketConnectionHandler.getSessionList().remove("player2");


            }
            if(gameId == null) {
                return null;
            }

            List<ChessState> chessStateList = chessRepository.getChessState(gameId);
            Collections.sort(chessStateList);
            List<List<ChessState>> l = new ArrayList<>();
            List<ChessState> l1 = new ArrayList<>();
            for (int j = 1; j <= chessStateList.size(); j++) {
                if(j % 64 == 0) {
                    l1.add(chessStateList.get(j - 1));
                    l.add(l1);
                    l1 = new ArrayList<>();
                }
                else {
                    l1.add(chessStateList.get(j - 1));
                }

            }
            chessStateList = l.get(l.size() - 1);
            for (int i = 0; i < 64; i++) {
                if(chessStateList.get(i).getPlayer2() == null) {
                    chessStateList.get(i).setPlayer2("player2");
                }
            }
                Collections.sort(chessStateList, Collections.reverseOrder());
                return chessStateList;

        }catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


    public List<ChessState> getState(String gameId, int index, int turn) {
        try{

            List<List<ChessState>> l = new ArrayList<>();
            List<ChessState> l1 = new ArrayList<>();


        List<ChessState> list = chessRepository.getState(gameId);
        Collections.sort(list);



         l = new ArrayList<>();
        List<ChessState> l2 = new ArrayList<>();
        for (int i = 1; i <= list.size(); i++) {

            if(i % 64 == 0) {
                l2.add(list.get(i - 1));
                l.add(l2);
              //  System.out.println(l);
                l2 = new ArrayList<>();
            }
            else {
                l2.add(list.get(i - 1));
               // System.out.println(l2);
            }
        }

             list = l.get(l.size() + index - 1);
           

                    return list;



        }catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }
}
