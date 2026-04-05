package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private String content;
    private Long createdAt;
    private Long updatedAt;

    public Message(UUID userId, UUID channelId, String content) {
        id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
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

    public void update(String content) {
        this.content = content;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Message {" +
                "\n id        = " + id +
                "\n userId    = " + userId +
                "\n channelId = " + channelId +
                "\n content   = " + content +
                "\n createdAt = " + createdAt +
                "\n updatedAt = " + updatedAt +
                "\n}\n";
    }
}