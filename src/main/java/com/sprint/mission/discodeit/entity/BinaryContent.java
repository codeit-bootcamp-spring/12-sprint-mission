package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private UUID messageId;
    private String contentType;
    private byte[] data;
    private Instant createdAt;

    public BinaryContent(UUID userId, UUID messageId, String contentType, byte[] data) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.messageId = messageId;
        this.contentType = contentType;
        this.data = data;
        this.createdAt = Instant.now();
    }
}
