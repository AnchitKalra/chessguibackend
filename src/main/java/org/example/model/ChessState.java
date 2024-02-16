package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;


import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "chessstate")
public class ChessState implements Comparable<ChessState>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;



    @Column(name = "boardValue")
    private Integer boardValue;



    @Column(name = "pieceValue")
    private Integer pieceValue;





    private String gameId;


    private String player1;


    private String player2;


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


    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    @Override
    public int compareTo(ChessState o) {
        return (this.id - o.id);
    }
}

