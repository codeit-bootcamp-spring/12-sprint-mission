package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private UUID id;
    private UUID userId;
    private Instant lastAccessedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId, Instant lastAccessedAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastAccessedAt = lastAccessedAt;
        this.createdAt = Instant.now();
    }

    public void update(Instant newLastAccessedAt){
        this.lastAccessedAt = newLastAccessedAt;
        this.updatedAt = Instant.now();
    }

    public boolean Online(){
        Instant now = Instant.now().minusSeconds(300);
        return lastAccessedAt.isAfter(now);

    }
}
