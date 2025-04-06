package com.javaee.test1.controllers;

public class MessageHolder {
    private static MessageHolder instance;
    private String lastMessage;

    private MessageHolder() {
    }

    public static MessageHolder getInstance() {
        if (instance == null) {
            instance = new MessageHolder();
        }
        return instance;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String message) {
        this.lastMessage = message;
    }
}

