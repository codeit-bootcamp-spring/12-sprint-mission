package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus {
    private final UUID id;
    private final UUID userId;
    private final Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateActiveTime() {
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return updatedAt.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
    }

}
