package com.sprint.mission.discodeit.entity;

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
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void update(Instant newLastActiveAt) {
        boolean anyValueUpdated = false;
        if (newLastActiveAt != null && !newLastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = newLastActiveAt;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        return lastActiveAt.isAfter(instantFiveMinutesAgo);
    }
}
