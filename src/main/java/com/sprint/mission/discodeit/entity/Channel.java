package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    UUID id;
    String name;
    String description;
    Long createdAt;
    Long updatedAt;

    public Channel(String name, String description) {
        long now = System.currentTimeMillis();

        id = UUID.randomUUID();
        createdAt = now;
        updatedAt = now;

        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String name, String description) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;

        updatedAt = System.currentTimeMillis();
    }

}
