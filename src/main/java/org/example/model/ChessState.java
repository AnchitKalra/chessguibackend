package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "chessstate")
public class ChessState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;



    @Column(name = "boardValue")
    private Integer boardValue;



    @Column(name = "pieceValue")
    private Integer pieceValue;




    @JsonProperty("gameId")
    private String gameId;


    public Integer getBoardValue() {
        return boardValue;
    }

    public void setBoardValue(Integer boardValue) {
        this.boardValue = boardValue;
    }

    public Integer getPieceValue() {
        return pieceValue;
    }

    public void setPieceValue(Integer pieceValue) {
        this.pieceValue = pieceValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}

