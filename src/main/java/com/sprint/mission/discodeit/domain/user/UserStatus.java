package com.sprint.mission.discodeit.domain.user;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private Instant lastActiveAt;

    private final UUID userId;



    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastActiveAt = lastActiveAt;
        this.userId = userId;
    }

    public void update(Instant lastActiveAt){
        this.lastActiveAt = lastActiveAt;
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return this.lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", lastActiveAt=" + lastActiveAt +
                ", userId=" + userId +
                '}';
    }
}

