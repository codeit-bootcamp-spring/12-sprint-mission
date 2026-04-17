package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    private final Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID id, UUID userId, UUID channelId) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.now();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public ReadStatus(UUID id, UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = id;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
        this.updatedAt = Instant.now();
    }
}
