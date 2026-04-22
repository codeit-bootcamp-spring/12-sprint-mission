package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private final Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.id = userId;
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isConnect() {
        Duration duration = Duration.between(this.updatedAt, Instant.now());
        return duration.toSeconds() <= 300;
    }

    public void update() {
        this.updatedAt = Instant.now();
    }
}
