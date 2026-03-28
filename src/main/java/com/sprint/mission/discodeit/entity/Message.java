package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    UUID id;
    UUID userId;
    UUID channelId;
    String content;
    long createdAt;
    long updatedAt;

    public Message(UUID userId, UUID channelId, String content) {
        long now = System.currentTimeMillis();

        id = UUID.randomUUID();
        createdAt = now;
        updatedAt = now;

        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String content, UUID channelId) {
        if (content != null) this.content = content;
        if (channelId != null) this.channelId = channelId;

        this.updatedAt = System.currentTimeMillis();
    }
}
