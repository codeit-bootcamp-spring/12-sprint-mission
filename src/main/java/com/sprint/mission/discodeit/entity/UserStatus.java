package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
public class UserStatus implements Serializable {
    private UUID id;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastSeenAt;

    @Builder
    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastSeenAt = Instant.now();
    }

    public void update(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return lastSeenAt != null && lastSeenAt.isAfter(Instant.now().minusSeconds(300));
    }
}