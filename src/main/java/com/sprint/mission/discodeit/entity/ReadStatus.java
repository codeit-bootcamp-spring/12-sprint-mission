package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "read_statuses")
public class ReadStatus extends BaseUpdatableEntity {

  private UUID userId;
  private UUID channelId;
  private Instant lastReadAt;

  protected ReadStatus() {
  }

  public ReadStatus(UUID userId,
                    UUID channelId,
                    Instant lastReadAt) {

    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = lastReadAt;
  }

  public void update(Instant newLastReadAt) {

    if (newLastReadAt != null &&
            !newLastReadAt.equals(this.lastReadAt)) {

      this.lastReadAt = newLastReadAt;
    }
  }
}