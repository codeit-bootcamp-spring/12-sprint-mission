package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String name;
    private String category;
    private String purpose;
    private String description;
    private String accessToken;
    private Long createdAt;
    private Long updatedAt;


    public Channel(String name, String category, String purpose, String description, String accessToken) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.category = category;
        this.purpose = purpose;
        this.description = description;
        this.accessToken = accessToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getDescription() {
        return description;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }


    public void update(String name, String category, String purpose, String description, String accessToken) {
        this.name = name;
        this.category = category;
        this.purpose = purpose;
        this.description = description;
        this.accessToken = accessToken;
        updatedAt = System.currentTimeMillis();
    }


    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", purpose='" + purpose + '\'' +
                ", description='" + description + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
