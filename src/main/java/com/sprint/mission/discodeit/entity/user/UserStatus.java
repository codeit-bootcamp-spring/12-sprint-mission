package com.sprint.mission.discodeit.entity.user;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        updatedAt = createdAt;

        this.userId = userId;
    }

    public void update() {
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return updatedAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }
}
