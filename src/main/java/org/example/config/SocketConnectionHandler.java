package org.example.config;



import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


@Service
public class SocketConnectionHandler extends TextWebSocketHandler {
  private static List<WebSocketSession> webSocketSessions
            = Collections.synchronizedList(new ArrayList<>());
  private  static  HashMap<String, String> sessionList = new HashMap<>();


  private static HashMap<WebSocketSession, Long> mapTime = new HashMap<>();


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
        mapTime.put(session, System.currentTimeMillis());
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
    public  void handleMessage(WebSocketSession session,
                              WebSocketMessage<?> message)
            throws Exception
    {

        super.handleMessage(session, message);

        // Iterate through the list and pass the message to
        // all the sessions Ignore the session in the list
        // which wants to send the message.
        String gameId = "";


            for (String keys : sessionList.keySet()) {
                    if(keys.equals(session.getId())) {
                        gameId = sessionList.get(keys);

                   break;
                }
            }


             A:   for (String keys : sessionList.keySet()) {
                    if(keys.equals(session.getId())) {
                        mapTime.put(session, System.currentTimeMillis());
                        continue;
                    }



                        String value = sessionList.get(keys);
                    if (value.equals(gameId)) {

                        System.out.println("MESSAGE SENT");

                        for (WebSocketSession webSocketSession : webSocketSessions) {
                            String s = webSocketSession.getId();
                            if(s.equals(keys)) {
                                webSocketSession.sendMessage(message);
                                mapTime.put(webSocketSession, System.currentTimeMillis());

                                break A;
                            }
                        }

                    }
                    }
                }




    public static void removeIdleSessions() {
        try {

            for (WebSocketSession session : webSocketSessions) {
                Long time = mapTime.get(session);

                if (Math.abs(System.currentTimeMillis() - time) > 120000) {
                    System.out.println("session closed" + session.getId());
                    webSocketSessions.remove(session);
                    sessionList.remove(session.getId());
                    mapTime.remove(session);
                    break;

                }
            }



        }catch (Exception e) {
            System.out.println(e);
        }
    }



   static   public  List<WebSocketSession> getWebSocketSessions() {
        return webSocketSessions;
    }
    static public  HashMap<String, String> getSessionList() {
        return sessionList;
    }


}
