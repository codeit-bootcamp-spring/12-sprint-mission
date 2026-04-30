package com.sprint.mission.discodeit.entity;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private UUID id;
    private UUID profileId;
    private String username;
    private String email;
    private String password;

    private Instant createdAt;
    private Instant updatedAt;

    public User(String username, String email, String password) {
        id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;

        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update(String username, String email, String password){
        boolean isUpdated = false;

        if (username != null && !username.isBlank()) {
            this.username = username;
            isUpdated = true;
        }

        if (email != null && !email.isBlank()) {
            this.email = email;
            isUpdated = true;
        }

        if (password != null && !password.isBlank()) {
            this.password = password;
            isUpdated = true;
        }

        if (isUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }
}