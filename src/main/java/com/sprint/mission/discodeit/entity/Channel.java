package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable,Comparable<Channel> {
    private final UUID id;
    private final User author;
    private String title;
    private String category;
    private final Long createdAt;
    private Long updatedAt;

    private static final long serialVersionUID = 1L;

    public Channel(String title, User author, String type) {
        id = UUID.randomUUID();
        this.title = title;
        this.author = author;
        this.category = type;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
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

    public void update(Channel channel){
        boolean isUpdated = false;

        if(channel.getTitle() != null){
            this.title = channel.getTitle();
            isUpdated = true;
        }
        if(channel.getCategory() != null){
            this.category = channel.getCategory();
            isUpdated = true;
        }

        if(isUpdated){
            this.updatedAt = System.currentTimeMillis();
        }
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
