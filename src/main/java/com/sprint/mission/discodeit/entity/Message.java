package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String content;
    private String sender;
    private String receiver;
    private Long createdAt;
    private Long updatedAt;

    public Message(String content, String sender, String receiver) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getCreatedAt() {
        return createdAt.toString();
    }

    public String getUpdatedAt() {
        return updatedAt.toString();
    }

    public void update(String content, String sender, String receiver) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "MessageService{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}



