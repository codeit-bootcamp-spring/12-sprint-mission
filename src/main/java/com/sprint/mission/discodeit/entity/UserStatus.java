package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {

    private UUID id;
    private UUID userId;

    private Instant lastSeenAt;
    private Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;

        this.lastSeenAt = Instant.now();
        this.createdAt = this.lastSeenAt;
        this.updatedAt = this.lastSeenAt;
    }

    public void updateLastSeen() {
        this.lastSeenAt = Instant.now();
        this.updatedAt = this.lastSeenAt;
    }

    public boolean isOnline() {
        return lastSeenAt.isAfter(Instant.now().minusSeconds(300));
    }
}