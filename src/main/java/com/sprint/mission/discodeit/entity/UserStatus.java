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
    private Instant lastSeenAt;

    public UserStatus(UUID userId, Instant lastSeenAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.lastSeenAt = lastSeenAt == null ? Instant.now() : lastSeenAt;
    }

    public void updateLastSeenAt(Instant newLastSeenAt) {
        if (newLastSeenAt != null && !newLastSeenAt.equals(this.lastSeenAt)) {
            this.lastSeenAt = newLastSeenAt;
            this.updatedAt = Instant.now();
        }
    }

    public boolean isOnline() {
        return lastSeenAt != null && lastSeenAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }
}
