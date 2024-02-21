package org.example.dao;


import jakarta.persistence.*;
import org.example.config.SocketConnectionHandler;
import org.example.model.ChessPieces;
import org.example.model.ChessState;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;

@Repository
public class ChessRepository {


    @PersistenceUnit(unitName = "chess")
    private EntityManagerFactory emf;


    public boolean savePieces(ChessPieces chessPieces) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        try {
            entityTransaction.begin();
            entityManager.merge(chessPieces);
            entityTransaction.commit();
            return true;
        }
        catch (Exception e) {
            System.out.println(e);
            entityTransaction.rollback();
        }
        finally {
            entityManager.close();
        }
        return false;
    }
    public boolean deleteChessPiece(ChessPieces chessPieces) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        Integer id = chessPieces.getId();
        try{
            entityTransaction.begin();
            TypedQuery<ChessPieces> typedQuery = entityManager.createQuery("delete from ChessPieces c where c.id = :id", ChessPieces.class);
            typedQuery.setParameter("id", id);
            entityTransaction.commit();
            return true;
        }
        catch (Exception e) {
            System.out.println(e);
            entityTransaction.rollback();
        }
        finally {
            entityManager.close();
        }
        return false;
    }

    public List<ChessPieces> getChessPieces() {
        EntityManager entityManager = emf.createEntityManager();
        try{
            TypedQuery<ChessPieces> typedQuery = entityManager.createQuery("select c from ChessPieces c", ChessPieces.class);
            return typedQuery.getResultList();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }



    public boolean saveAndGetState(int boardValue, int pieceValue, String gameId, List<WebSocketSession> sessions) {
       // System.out.println("SAVE & GET STATE");
        ChessState chessState = new ChessState();
       // System.out.println(boardValue);
        chessState.setBoardValue(boardValue);
       // System.out.println(pieceValue);
        chessState.setPieceValue(pieceValue);
       // System.out.println(gameId);
        chessState.setGameId(gameId);
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        List<WebSocketSession> list = SocketConnectionHandler.getWebSocketSessions();


        HashMap<String, String > map = SocketConnectionHandler.getSessionList();


        if(list.size() > map.size()) {

            map.put(list.get(list.size() - 1).getId(), gameId);
            SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), gameId);

        }

        if(!list.isEmpty()) {
            if(list.size() % 2 == 0) {

                    for (WebSocketSession session : list) {
                        String s = session.getId();
                        if(map.get(s).equals(gameId)) {
                            chessState.setPlayer1(s);
                            break;
                        }

                }
                for (WebSocketSession session : list) {
                    String s = session.getId();
                    if(chessState.getPlayer1() != null) {
                    if(chessState.getPlayer1().equals(s)) {
                        continue;
                    }
                    if(map.get(s).equals(gameId)) {
                        chessState.setPlayer2(s);
                    }}
                }
            }
            else {

                for (WebSocketSession session : list) {
                    String s = session.getId();
                    if(map.get(s).equals(gameId)) {
                        chessState.setPlayer1(s);
                    }
                }
                if(chessState.getPlayer1() == null) {
                    chessState.setPlayer1("player1");
                }
            }


        }else {
            chessState.setPlayer1("player1");
        }

        try{
            transaction.begin();
            entityManager.merge(chessState);
            transaction.commit();
            return true;
        }
        catch (Exception e) {

            System.out.println(e);
            transaction.rollback();
        }

        return false;
    }


    public ChessState retreiveState(ChessState state) {
        EntityManager entityManager = emf.createEntityManager();
        int boardValue = state.getBoardValue();
        int pieceValue = state.getPieceValue();
        String gameId = state.getGameId();
        try{
            TypedQuery<ChessState> typedQuery = entityManager.createQuery("select cs from ChessState cs where cs.boardValue = :boardValue and cs.pieceValue = :pieceValue and cs.gameId = :gameId", ChessState.class);
            typedQuery.setParameter("boardValue", boardValue);
            typedQuery.setParameter("pieceValue", pieceValue);
            typedQuery.setParameter("gameId", gameId);
            return typedQuery.getSingleResult();
        }catch (Exception e) {
            System.out.println(e);
            return null;
        }
        finally {
            if(boardValue == 63) {
                entityManager.close();
            }
        }
    }

    public List<ChessState> getChessState(String gameId) {
        EntityManager entityManager = emf.createEntityManager();
        try{
            TypedQuery<ChessState> typedQuery = entityManager.createQuery("select cs from ChessState cs where cs.gameId = :gameId", ChessState.class);
            typedQuery.setParameter("gameId", gameId);
            return (typedQuery.getResultList());
        }
        catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public boolean deleteState(String gameId) {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try{
            transaction.begin();
            Query query = entityManager.createQuery("delete from ChessState cs where cs.gameId = :gameId");
            query.setParameter("gameId", gameId);
            query.executeUpdate();
            transaction.commit();
            return true;
        }
        catch (Exception e) {
            System.out.println(e);
            transaction.rollback();
        }

        return false;
    }


    public List<ChessState> getState(String gameId) {
        EntityManager entityManager = emf.createEntityManager();
        try{
            TypedQuery<ChessState> typedQuery = entityManager.createQuery("select cs from ChessState cs where cs.gameId = :gameId", ChessState.class);
            typedQuery.setParameter("gameId", gameId);
            return typedQuery.getResultList();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }



}
