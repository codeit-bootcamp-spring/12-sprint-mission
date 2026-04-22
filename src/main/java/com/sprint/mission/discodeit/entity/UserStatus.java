package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String userId;
    private Instant lastSeenAt;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isOnline() {
        Instant now = Instant.now();
        if (lastSeenAt == null) {
            return false;
        }
        Duration duration = Duration.between(lastSeenAt, now);
        return duration.toMinutes() <= 5;
    }
    public UserStatus(String id, String userId, Instant lastSeenAt) {
        this.id = id;
        this.userId = userId;
        this.lastSeenAt = lastSeenAt;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        }
    public void updateLastSeenAt() {
        this.lastSeenAt = Instant.now();
        }
}


