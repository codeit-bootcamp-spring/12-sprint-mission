package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
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

    public UUID getId() { return this.id; }
    public String getContent() { return content; }
    public String getSender() {
        return this.sender;
    }
    public String getReceiver() {
        return this.receiver;
    }
    public Long getCreatedAt() { return this.createdAt; }
    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            this.updatedAt = System.currentTimeMillis();
        }
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



