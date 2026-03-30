package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private String name;      // 채팅방 이름
    private Long createdAt;
    private Long updatedAt;


    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }


    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }


    public void update(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
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
}