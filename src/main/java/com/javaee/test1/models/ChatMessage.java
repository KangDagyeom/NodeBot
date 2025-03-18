package com.javaee.test1.models;

import java.sql.Timestamp;
import java.util.UUID;

public class ChatMessage {
    private UUID messageID;
    private UUID conversationID;
    private UUID senderID;
    private String senderType;
    private String messageText;
    private String messageType;
    private Timestamp sentAt;

    public ChatMessage(UUID messageID, UUID conversationID, UUID senderID, String senderType, String messageText, String messageType, Timestamp sentAt) {
        this.messageID = messageID;
        this.conversationID = conversationID;
        this.senderID = senderID;
        this.senderType = senderType;
        this.messageText = messageText;
        this.messageType = messageType;
        this.sentAt = sentAt;
    }

    public UUID getMessageID() {
        return messageID;
    }

    public void setMessageID(UUID messageID) {
        this.messageID = messageID;
    }

    public UUID getConversationID() {
        return conversationID;
    }

    public void setConversationID(UUID conversationID) {
        this.conversationID = conversationID;
    }

    public UUID getSenderID() {
        return senderID;
    }

    public void setSenderID(UUID senderID) {
        this.senderID = senderID;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
}
