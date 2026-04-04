package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String content;
    private final Long createdAt;
    private Long updatedAt;

    private final UUID channelId;
    private final UUID userId;

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
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtStr = formatter.format(Instant.ofEpochMilli(createdAt));
        String updatedAtStr = formatter.format(Instant.ofEpochMilli(updatedAt));

        return "id: " + id + "\n" +
                "content: " + content + "\n" +
                "createdAt: " + createdAtStr + "\n" +
                "updatedAt: " + updatedAtStr + "\n" +
                "channelId: " + channelId + "\n" +
                "userId: " + userId + "\n";
    }
}
