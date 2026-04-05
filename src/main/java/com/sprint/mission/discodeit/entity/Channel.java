package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {
    private UUID id;
    private String name;
    private String description;

    // 사용자 목록
    private List<User> users;

    private Long createdAt;
    private Long updatedAt;

    public Channel(String name, String description) {
        this.id = UUID.randomUUID();

        this.name = name;
        this.description = description;

        this.users = new ArrayList<>();

        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

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

    public List<User> getUsers() {
        return users;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    // 채널에 사용자 들어오기
    public void addUser(User user) {
        this.users.add(user);
    }

    // 채널에 사용자 나가기
    public void removeUser(User user) {
        this.users.remove(user);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", users=" + users +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
