package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    private final UUID id;
    private final byte[] bytes;
    private final String fileName;
    private final String contentType;
    private final Instant createdAt;

    public BinaryContent(byte[] bytes, String fileName, String contentType) {
        this.id = UUID.randomUUID();
        this.bytes = bytes;
        this.fileName = fileName;
        this.contentType = contentType;
        this.createdAt = Instant.now();
    }
}