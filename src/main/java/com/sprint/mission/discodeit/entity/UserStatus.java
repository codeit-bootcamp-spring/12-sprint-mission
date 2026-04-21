package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private Instant lastAccessedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.userId = userId;
        this.lastAccessedAt = Instant.now();
    }

    public boolean isUserOnline() {
        return !Instant.now().isAfter(lastAccessedAt.plusSeconds(300L));
    }

    public void update(Instant accessedAt) {
        lastAccessedAt = accessedAt;
        updatedAt = Instant.now();
    }
}
