package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private final UUID id;
    private final String fileName;
    private final String url;
    private final Instant createdAt;

    @Serial
    private static final long serialVersionUID = 1L;

    public BinaryContent( String fileName, String url) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.url = url;
        this.createdAt = Instant.now();
    }

}

