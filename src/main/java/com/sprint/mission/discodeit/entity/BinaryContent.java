package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private UUID id;
    private Instant createdAt;

    private String fileName;
    private String contentType;
    private byte[] bytes;

    public BinaryContent(UUID id, String fileName, String contentType, byte[] bytes) {
        this.id = id;
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }
}
