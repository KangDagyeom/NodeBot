package com.javaee.test1.models;

import java.sql.Timestamp;
import java.util.UUID;

public class ChatMessage {
    private UUID messageId;
    private UUID senderId;
    private UUID receiverId;
    private String messageText;
    private boolean hasAttachment;
    private String attachmentFileName;
    private String attachmentFilePath;
    private Timestamp sentAt;

    public ChatMessage(UUID messageId, UUID senderId, UUID receiverId, String messageText, boolean hasAttachment, String attachmentFileName, String attachmentFilePath, Timestamp sentAt) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.hasAttachment = hasAttachment;
        this.attachmentFileName = attachmentFileName;
        this.attachmentFilePath = attachmentFilePath;
        this.sentAt = sentAt;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentFilePath() {
        return attachmentFilePath;
    }

    public void setAttachmentFilePath(String attachmentFilePath) {
        this.attachmentFilePath = attachmentFilePath;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
}
