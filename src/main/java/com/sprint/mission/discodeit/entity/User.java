package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id; // final로 선언해도 문제 없는것 같은데 일단 보류
    private String username;
    private String email;
    private String password;
    private String nickname;
    private Long createdAt; // id 와 동일
    private Long updatedAt;

    public User(String username, String email, String password, String nickname) {
        long now = System.currentTimeMillis();

        id = UUID.randomUUID();
        createdAt = now;
        updatedAt = now;

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

    public void update(String username, String email, String password, String nickname) {
        if (username != null) this.username = username;
        if (email != null) this.email = email;
        if (password != null) this.password = password;
        if (nickname != null) this.nickname = nickname;

        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() { // 어떻게 수정할지
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
