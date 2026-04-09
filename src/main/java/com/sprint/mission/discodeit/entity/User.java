package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
public class User implements Serializable {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID profileImageId;

    @Builder
    public User(String username, String email, String password, String nickname, UUID profileImageId) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageId = profileImageId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update(String username, String email, String password, String nickname, UUID profileImageId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageId = profileImageId;
        this.updatedAt = Instant.now();
    }
}