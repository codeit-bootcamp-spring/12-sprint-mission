package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel implements Serializable,Comparable<Channel> {
    private final UUID id;
    private final User author;
    private String title;
    private String category;
//    private List<User> userList = new ArrayList<>();
    private final long createdAt;
    private long updatedAt;

    private static final long serialVersionUID = 1L;

    public Channel(String title, User author, String type) {
        id = UUID.randomUUID();
        this.title = title;
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

    public String getTitle() {
        return title;
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

    public void updateCategory(String category) {
        this.category = category;
        updatedAt = System.currentTimeMillis();
    }

    public void updateTitle(String title) {
        this.title = title;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", author=" + author.getName() +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}'+"\n";
    }

    public int compareTo(Channel o) {
        return o.category.compareTo(category);
    }
}
