package com.javaee.test1.controllers;

public class ConversationItem {
    private String uuid;
    private String title;

    public ConversationItem(String uuid, String title) {
        this.uuid = uuid;
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}

