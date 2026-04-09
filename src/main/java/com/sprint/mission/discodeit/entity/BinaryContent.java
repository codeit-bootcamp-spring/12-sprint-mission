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
public class BinaryContent implements Serializable {
    private UUID id;
    private UUID userId;
    private UUID messageId;
    private byte[] binaryData;
    private Instant createdAt;

    @Builder
    public BinaryContent(UUID userId, UUID messageId, byte[] binaryData) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.messageId = messageId;
        this.binaryData = binaryData;
        this.createdAt = Instant.now();
    }
}