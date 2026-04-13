package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable, Comparable<User> {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private UUID profileId;
    private Instant createdAt;
    private Instant updatedAt;

    public User(String username, String email, String password, String nickname, UUID profileId) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileId = profileId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update(String username, String email, String password, String nickname, UUID profileId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "User {" +
                "\n id        = " + id +
                "\n username  = " + username +
                "\n email     = " + email +
                "\n password  = " + password +
                "\n nickname  = " + nickname +
                "\n profileId = " + profileId +
                "\n createdAt = " + createdAt +
                "\n updatedAt = " + updatedAt +
                "\n}\n";
    }

    @Override
    public int compareTo(User o) {
        return this.username.compareTo(o.username);
    }
}
