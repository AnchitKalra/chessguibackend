package org.example.dao;


import org.example.config.SocketConnectionHandler;
import org.example.model.ChessPieces;
import org.example.model.ChessState;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

import static org.example.config.SocketConnectionHandler.getSessionList;

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

        if(!list.isEmpty()) {

            if(list.size() % 2 == 1) {
                try {

                    chessState.setPlayer1(list.get(list.size() - 1).getId());
                    System.out.println("from list.size() % 2 == 1" + chessState.getPlayer1());

                    if (SocketConnectionHandler.getSessionList().isEmpty() ) {
                        SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), gameId);
                    }
                    else if(!SocketConnectionHandler.getSessionList().containsKey(list.get(list.size() - 1).getId())) {
                        SocketConnectionHandler.getSessionList().put(list.get(list.size() - 1).getId(), gameId);
                    }
                }catch (Exception e) {System.out.println(e);
                    System.out.println("from list.size % 2 from chess repo");
                }




            }

         else   {
             SocketConnectionHandler.getSessionList().clear();
                chessState.setPlayer1(list.get(list.size() - 2).getId());
                if(!SocketConnectionHandler.getSessionList().containsKey(list.get(list.size() - 2).getId())) {
                    SocketConnectionHandler.getSessionList().put(list.get(list.size() - 2).getId(), gameId);
                }

                chessState.setPlayer2(list.get(list.size() - 1).getId());
                if(!SocketConnectionHandler.getSessionList().containsKey(list.get(list.size() - 1).getId())) {
                    SocketConnectionHandler.getSessionList().put((list.get(list.size() - 1).getId()), gameId);
                }
            }
        }
        else {
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
        finally {
            if(boardValue == 63) {
                entityManager.close();
            }
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
            return typedQuery.getResultList();
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
        finally {
            entityManager.close();
        }
        return false;
    }


//    public List<Integer> getState() {
//        EntityManager entityManager = emf.createEntityManager();
//        try{
//        }
//    }
}
