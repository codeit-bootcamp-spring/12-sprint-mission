package com.sprint.mission.discodeit.entity.user;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private String username;
    private String password;
    private String email;
    private UUID profileId;

    public User(String username, String password, String email , UUID profileId) {
        id = UUID.randomUUID();
        createdAt = Instant.now();
        updatedAt = createdAt;

        this.username = username;
        this.password = password;
        this.email = email;
        this.profileId = profileId;
    }

    public void update(String username, String password, String email, UUID profileId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileId = profileId;

        this.updatedAt = Instant.now();
    }
}