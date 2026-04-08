package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private String content;
    private boolean isEdited;
    private final User sendUser;

    public Message(String content, User sendUser) {
        id = UUID.randomUUID();
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
        this.content = content;
        isEdited = false;
        this.sendUser = sendUser;
    }

    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getContent() { return content; }
    public boolean isEdited() { return isEdited; }
    public User getSendUser() { return sendUser; }

    public void update(String content) {
        this.content = content;
        isEdited = true;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Message message = (Message) object;
        return isEdited == message.isEdited && Objects.equals(id, message.id) && Objects.equals(createdAt, message.createdAt) && Objects.equals(updatedAt, message.updatedAt) && Objects.equals(content, message.content) && Objects.equals(sendUser, message.sendUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, content, isEdited, sendUser);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                ", isEdited=" + isEdited +
                ", sendUser=" + sendUser +
                '}';
    }
}
