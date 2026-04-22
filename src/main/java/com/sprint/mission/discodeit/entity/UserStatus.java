package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;
    private LocalDateTime lastConnectedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastConnectedAt = LocalDateTime.now();
    }

    public boolean isOnline() {
        if(this.lastConnectedAt == null){
            return false;
        }

        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        return this.lastConnectedAt.isAfter(fiveMinutesAgo);
    }

    public void updateConnection(LocalDateTime lastConnectedAt) {
        if (lastConnectedAt == null) {
            throw new IllegalArgumentException("수정할 접속 시각 데이터가 없습니다");
        }
        this.lastConnectedAt = lastConnectedAt;
        this.updatedAt = Instant.now();
    }
}
