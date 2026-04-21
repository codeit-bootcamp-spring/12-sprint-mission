package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    private UUID id;

    private String fileName;
    private String contentType;
    private byte[] data;

    private UUID userId;     // 프로필 이미지용
    private UUID messageId;  // 메시지 첨부파일용

    private Instant createdAt;

    public BinaryContent(String fileName, String contentType, byte[] data,
                         UUID userId, UUID messageId) {

        this.id = UUID.randomUUID();

        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;

        this.userId = userId;
        this.messageId = messageId;

        this.createdAt = Instant.now();
    }
}