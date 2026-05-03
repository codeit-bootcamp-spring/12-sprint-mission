package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
// 마지막 접속 시간 기반으로 온라인 판단
// 5분 이내 → 온라인
@Getter
public class UserStatus implements  Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void update(Instant lastActiveAt) {
        boolean anyValueUpdated = false;
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public Boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

        return lastActiveAt.isAfter(instantFiveMinutesAgo);
    }
}
