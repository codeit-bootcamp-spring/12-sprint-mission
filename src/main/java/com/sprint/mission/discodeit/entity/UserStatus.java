package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private UUID id;
    private UUID userId;

    private Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID id, UUID userId) {
        this.id = id;
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(){
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return Duration.between(updatedAt, Instant.now()).toMillis() < 1000 * 60 * 5;
    }
}
