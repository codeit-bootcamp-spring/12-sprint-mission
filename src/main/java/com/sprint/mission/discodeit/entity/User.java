package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private UUID id;
    private UUID profileId;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private Instant createdAt;
    private Instant updatedAt;

    public User(String username, String email, String password, String nickname) {
        this.id = UUID.randomUUID();
        this.profileId = null;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String username, String email, String password, String nickname) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.updatedAt = Instant.now();
    }

    public void updateProfile(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime created = LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault());
        LocalDateTime updated = LocalDateTime.ofInstant(updatedAt, ZoneId.systemDefault());

        return "[User]" +
                "\n- ID: " + id +
                "\n- ProfileId: " + profileId +
                "\n- Username: " + username +
                "\n- Email: " + email +
                "\n- Nickname: " + nickname +
                "\n- CreatedAt: " + created.format(formatter) +
                "\n- UpdatedAt: " + updated.format(formatter) +
                "\n";
    }
}