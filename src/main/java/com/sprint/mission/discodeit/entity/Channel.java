package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private final User author;
    private String category;
//    private List<User> userList = new ArrayList<>();
    private final long createdAt;
    private long updatedAt;

    public Channel(User author, String type, User user) {
        id = UUID.randomUUID();
        this.author = author;
        this.category = type;
//        userList.add(user);
        createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setCategory(String category) {
        this.category = this.category;
        updatedAt = System.currentTimeMillis();
    }

}
