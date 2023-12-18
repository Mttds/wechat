package com.example.chat;

import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Server
 *
 * @see ServerEndpoint WebSocket Client
 * @see Session WebSocket Session
 *      We declare a Java class WebSocketChatServer server endpoint by
 *      annotating it with @ServerEndpoint.
 *      We also specify the URI where the endpoint is deployed (/chat).
 *      The URI is defined relatively to the root of the server container and
 *      must begin with a forward slash
 * 
 * @ServerEndpoint: If decorated with @ServerEndpoint, the container ensures
 *                  availability of the class
 *                  as a WebSocket server listening to a specific URI space
 * @ClientEndpoint: A class decorated with this annotation is treated as a
 *                  WebSocket client
 * @OnOpen: A Java method with @OnOpen is invoked by the container when a new
 *          WebSocket connection is initiated
 * @OnMessage: A Java method, annotated with @OnMessage, receives the
 *             information from the WebSocket container
 *             when a message is sent to the endpoint
 * @OnError: A method with @OnError is invoked when there is a problem with the
 *           communication
 * @OnClose: Used to decorate a Java method that is called by the container when
 *           the WebSocket connection closes
 */

@Component
@ServerEndpoint("/index/{username}")
public class WebSocketChatServer {
    /*
     * All chat sessions.
     */
    private Session session;
    private static Message.Action actionType;
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    private static void sendMessageToAll(String msg) {
        for (String key : onlineSessions.keySet()) {
            Session sessionObject = onlineSessions.get(key);
            if (sessionObject.isOpen()) {
                try {
                    System.out.println("[INFO] - Session " + sessionObject.getId() + " is saying: " + msg);
                    sessionObject.getBasicRemote().sendText(msg);
                } catch (IOException exception) {
                    exception.printStackTrace();
                    System.out.println("[ERROR] - Caught exception while sending message to Session Id: " + sessionObject.getId());
                }
            }
        }
    }

    /*
     * public static void broadcast(String msg) {
     * for (WebSocketChatServer listener : listeners) {
     * listener.sendMessageToAll(msg);
     * }
     * }
     */

    /*
     * Open connection, 1) add session, 2) add user.
     */
    @OnOpen // @PathParam username is added to the URI
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        System.out.println("[INFO] - Calling onOpen() method for session " + session.getId());
        // on a new connection put a new pair in the hashmap with sessionId and session object
        this.session = session;
        // onlineSessions.put(session.getId(), session);
        onlineSessions.put(username, session);
        System.out.println("[INFO] - User " + username + " has joined the chat.");
        // listeners.add(this);
        Message message = new Message(); // call argument constructor
        message.setType(Message.Action.ENTER);
        message.setMsg("Session id " + session.getId() + " connected for user " + username + ".");
        message.setSender(username);
        sendMessageToAll(Message.jsonConverter(message.getMsg(), message.getSender(), message.getType(), onlineSessions.size()));
    }

    /*
     * Send message, 1) get username and session, 2) send message to all.
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) {
        System.out.println("[INFO] - Calling onMessage() method for session " + session.getId());
        Message message = (Message) JSON.parseObject(jsonStr, Message.class);
        sendMessageToAll(Message.jsonConverter(message.getMsg(), message.getSender(), Message.Action.CHAT, onlineSessions.size()));

    }

    /*
     * Close connection, 1) remove session, 2) update user.
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        System.out.println("[INFO] - Calling onClose() method for session " + session.getId());
        // listeners.remove(this);
        onlineSessions.remove(username);
        sendMessageToAll(
            Message.jsonConverter(
                "User " + username + " left the chat.",
                username,
                Message.Action.LEAVE,
                onlineSessions.size()
            )
        );
    }

    /*
     * Print exception.
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("[ERROR] - Calling onError() method for session " + session.getId());
        error.printStackTrace();
    }
}
