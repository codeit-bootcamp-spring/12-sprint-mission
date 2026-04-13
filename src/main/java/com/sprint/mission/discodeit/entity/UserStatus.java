package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;
    private Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.lastActiveAt = Instant.now();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update() {
        this.lastActiveAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

    @Override
    public String toString() {
        return "UserStatus {" +
                "\n id        = " + id +
                "\n userId    = " + userId +
                "\n createdAt = " + createdAt +
                "\n updatedAt = " + updatedAt +
                "\n}\n";
    }
}
