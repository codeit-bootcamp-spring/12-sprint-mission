package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private String content;
    private Long createdAt;
    private Long updatedAt;

    private UUID channelId;
    private UUID userId;

    public Message(String content, UUID channelId, UUID userId) {
        long now = System.currentTimeMillis();

        this.id = UUID.randomUUID();
        this.content = content;
        this.channelId = channelId;
        this.userId = userId;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void update(String content){
        this.content = content;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelId=" + channelId +
                ", userId=" + userId +
                '}';
    }
}
