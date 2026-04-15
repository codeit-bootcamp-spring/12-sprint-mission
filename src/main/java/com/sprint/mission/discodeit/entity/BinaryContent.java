package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private UUID id;
    private Instant createdAt;

    private UUID userId;
    private UUID messageId;

    public BinaryContent(UUID id, UUID userId, UUID messageId) {
        this.id = id;
        this.createdAt = Instant.now();
        this.userId = userId;
        this.messageId = messageId;
    }
}
