package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private String name;
    private Long createdAt;
    private Long updatedAt;

    public Channel(String name) {
        long now = System.currentTimeMillis();

        this.id = UUID.randomUUID();
        this.name = name;
        createdAt = now;
        updatedAt = now;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public void update(String name){
        this.name = name;
        updatedAt = System.currentTimeMillis();
    }


}
