package com.example.chat;

import com.alibaba.fastjson.JSON;

/*
 * WebSocket message model
*/
public class Message {
    private String msg;
    private String sender;
    private Action type;
    private int onlineUsers;

    // enumeration for the type of message
    public enum Action {
        CHAT,
        ENTER,
        LEAVE
    }

    // Default constructor
    public Message() {
    }

    // Constructor
    public Message(String msg, String sender, Action type, int onlineUsers) {
        this.msg = msg;
        this.sender = sender;
        this.type = type;
        this.onlineUsers = onlineUsers;
    }

    // getters and setters
    public String getMsg() {
        return this.msg;
    }

    public Action getType() {
        return this.type;
    }

    public String getSender() {
        return this.sender;
    }

    public int getOnlineUsers() {
        return this.onlineUsers;
    }

    public void setMsg(String new_msg) {
        this.msg = new_msg;
    }

    public void setType(Action new_type) {
        this.type = new_type;
    }

    public void setSender(String new_sender) {
        this.sender = new_sender;
    }

    public void setOnlineUsers(int new_onlineUsers) {
        this.onlineUsers = new_onlineUsers;
    }

    // Method for converting a message object to a string
    public static String jsonConverter(String msg, String sender, Action type, int onlineUsers) {
        Message message = new Message(msg, sender, type, onlineUsers);
        return JSON.toJSONString(message);
    }
}
