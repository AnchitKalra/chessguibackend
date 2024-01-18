package org.example.config;



import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class SocketConnectionHandler extends TextWebSocketHandler {
    static List<WebSocketSession> webSocketSessions
            = Collections.synchronizedList(new ArrayList<>());
  private  static  HashMap<String, String> sessionList = new HashMap<>();


    @Override
    public void
    afterConnectionEstablished(WebSocketSession session)
            throws Exception
    {

        super.afterConnectionEstablished(session);
        // Logging the connection ID with Connected Message
        System.out.println(session.getId() + " Connected");

        // Adding the session into the list
        webSocketSessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status)throws Exception
    {
        super.afterConnectionClosed(session, status);
        System.out.println(session.getId()
                + " DisConnected");
        if(sessionList.containsKey(session.getId())) {
            System.out.println(sessionList.get(session.getId()) + "  removed");
            sessionList.remove(session.getId());

        }
        sessionList.remove("player2");

        // Removing the connection info from the list
        webSocketSessions.remove(session);
    }


    @Override
    public void handleMessage(WebSocketSession session,
                              WebSocketMessage<?> message)
            throws Exception
    {

        super.handleMessage(session, message);

        // Iterate through the list and pass the message to
        // all the sessions Ignore the session in the list
        // which wants to send the message.
        for (WebSocketSession webSocketSession :
                webSocketSessions) {
            if (session == webSocketSession)
                continue;


            // sendMessage is used to send the message to
            // the session
            webSocketSession.sendMessage(message);
        }
    }


  static   public  List<WebSocketSession> getWebSocketSessions() {
        return webSocketSessions;
    }
    static public  HashMap<String, String> getSessionList() {
        return sessionList;
    }


}
