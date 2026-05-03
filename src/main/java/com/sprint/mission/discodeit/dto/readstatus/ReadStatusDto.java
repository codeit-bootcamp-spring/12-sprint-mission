package com.sprint.mission.discodeit.dto.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto(
    UUID id,
    UUID userId,
    UUID channelId,
    Instant createdAt,
    Instant updatedAt,
    Instant lastLeadAt
) {

  public static ReadStatusDto from(ReadStatus readStatus) {
    return new ReadStatusDto(
        readStatus.getId(),
        readStatus.getUserId(),
        readStatus.getChannelId(),
        readStatus.getCreatedAt(),
        readStatus.getUpdatedAt(),
        readStatus.getLastReadAt()
    );
  }
}
