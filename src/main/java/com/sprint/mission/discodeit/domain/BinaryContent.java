package com.sprint.mission.discodeit.domain;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;

    private final String mimeType;
    private final byte[] data;
    private final String filename;

    public BinaryContent(byte[] data, String filename,String mimeType ) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.data = data;
        this.filename = filename;
        this.mimeType = mimeType;
    }


}
