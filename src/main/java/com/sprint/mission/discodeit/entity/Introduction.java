package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Introduction {
    private UUID id;
    private String bio;
    private String profileImage;
    private Long createdAt;
    private Long updatedAt;

    public Introduction(String bio, String ProfileImage) {
        id = UUID.randomUUID();
        this.bio = bio;
        this.profileImage = profileImage;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public String getBio() {
        return bio;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }


    public void update(String bio, String profileImage) {
        this.bio = bio;
        this.profileImage = profileImage;
        updatedAt = System.currentTimeMillis();


    }

    @Override
    public String toString() {
        return "Introduction{" +
                "id=" + id +
                ", bio='" + bio + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}







