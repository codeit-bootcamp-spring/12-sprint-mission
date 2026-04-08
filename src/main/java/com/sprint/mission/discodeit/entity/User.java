package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private final UUID userId;
    private String username;
    private String email;
    private String password;
    private final Long createdAt;
    private Long updatedAt;

    public User(String username, String email, String password) {
        userId = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUsername(String username) {
        this.username = username;
        updatedAt = System.currentTimeMillis();
    }

    public void updateEmail(String email) {
        this.email = email;
        updatedAt = System.currentTimeMillis();
    }

    public void updatePassword(String password) {
        this.password = password;
        updatedAt = System.currentTimeMillis();
    }


    public void update(String username, String email, String password, String nickname, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
               updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}