package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private String username;
    private String email;
    private String password;
    private String nickname;

    public User(String username, String email, String password, String nickname) {
        id = UUID.randomUUID();
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
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

    public void update(String username, String email, String password, String nickname){
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(id, user.id) && Objects.equals(createdAt, user.createdAt) && Objects.equals(updatedAt, user.updatedAt) && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(nickname, user.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, username, email, password, nickname);
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
