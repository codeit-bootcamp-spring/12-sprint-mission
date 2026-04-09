package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private Long createdAt;
    private Long updatedAt;

    public User(String username, String email, String password, String nickname) {
        id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
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

    public String getNickname() {
        return nickname;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }


    public void update(String newUsername, String newEmail, String newPassword, String newNickname) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
            if (newEmail != null && !newEmail.equals(this.email)) {
                this.email = newEmail;
                anyValueUpdated = true;
            }
            if (newPassword != null && !newPassword.equals(this.password)) {
                this.password = newPassword;
                anyValueUpdated = true;
            }
            if (anyValueUpdated) {
                this.updatedAt = System.currentTimeMillis();
            }
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


