package org.example.service;


import org.example.dao.ChessRepository;
import org.example.model.ChessPieces;
import org.example.model.ChessState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ChessService {


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


    public List<ChessState> saveState(List<Integer> idList, List<Integer> pieceValueList, String gameId) {
        int i = 0;
        List<ChessState> chessStateList = new ArrayList<>();
        if(gameId == "") {
            UUID uuid = UUID.randomUUID();
            gameId = uuid.toString();
            for (int pieceValue : pieceValueList) {
                if(chessRepository.saveAndGetState(i++, pieceValue, gameId)) {

                }
                else{
                    break;
                }
            }
        }
        else{
                if(chessRepository.deleteState(gameId)) {
                    for (int pieceValue : pieceValueList) {
                        System.out.println("FROM CHESS SERVICE " + " PIECEVALUELIST " + pieceValueList);
                        if(chessRepository.saveAndGetState(i++, pieceValue, gameId)) {
                            System.out.println("I");
                            System.out.println(i);
                            System.out.println(pieceValue);
                            System.out.println(gameId);
                        }
                        else {
                            break;
                        }
                    }
                }
        }
        chessStateList = chessRepository.getChessState(gameId);
        if(chessStateList.size() == 64) {
            return chessStateList;
        }
        else {
            return null;
        }

    }

    public List<ChessState> retreiveState(String gameId) {
        return chessRepository.getChessState(gameId);
    }


}
