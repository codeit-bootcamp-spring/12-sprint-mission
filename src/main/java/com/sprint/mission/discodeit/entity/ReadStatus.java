package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update(Instant newTimestamp) {
        if (newTimestamp == null) {
            throw new IllegalArgumentException("수정할 시각 데이터가 없습니다.");
        }
        if (newTimestamp.isBefore(this.createdAt)) {
            throw new IllegalArgumentException("업데이트 시각은 생성 시각보다 빠를 수 없습니다.");
        }
        this.updatedAt = newTimestamp;
    }
}
