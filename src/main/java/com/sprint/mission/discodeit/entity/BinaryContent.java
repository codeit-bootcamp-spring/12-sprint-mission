package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private byte[] content;
    private String contentType;
    private Long contentSize;

    public BinaryContent(String contentType, byte[] content) {
        this.id = UUID.randomUUID();
        this.contentType = contentType;
        this.content = content;
        this.contentSize = (long)content.length;
        this.createdAt = Instant.now();
    }

    public void update(String contentType, byte[] content) {
        this.content = content;
        this.contentType = contentType;
        this.contentSize = (long)content.length;
    }
}
