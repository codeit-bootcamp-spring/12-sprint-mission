package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.UUID;

public class Channel {
    private UUID id;
    private User author;
    private String type;
    private long createdAt;
    private long updatedAt;

    public Channel(User author, String type) {
        id = UUID.randomUUID();
        this.author = author;
        this.type = type;
        createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getType() {
        return type;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setType(String type) {
        this.type = type;
        updatedAt = System.currentTimeMillis();
    }

}
