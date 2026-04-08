package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable, Comparable<User> {
    @Serial
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

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public UUID getId() {
        return id;
    }

    public void update(String username, String email, String password, String nickname) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "User {" +
                "\n id        = " + id +
                "\n username  = " + username +
                "\n email     = " + email +
                "\n password  = " + password +
                "\n nickname  = " + nickname +
                "\n createdAt = " + createdAt +
                "\n updatedAt = " + updatedAt +
                "\n}\n";
    }

    @Override
    public int compareTo(User o) {
        return this.username.compareTo(o.username);
    }
}
