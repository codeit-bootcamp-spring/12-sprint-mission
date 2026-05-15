package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

  private UUID userId;
  private Instant lastActiveAt;

  protected UserStatus() {
  }

  public UserStatus(UUID userId, Instant lastActiveAt) {
    this.userId = userId;
    this.lastActiveAt = lastActiveAt;
  }

  public void update(Instant lastActiveAt) {

    if (lastActiveAt != null &&
            !lastActiveAt.equals(this.lastActiveAt)) {

      this.lastActiveAt = lastActiveAt;
    }
  }

  public Boolean isOnline() {

    Instant instantFiveMinutesAgo =
            Instant.now().minus(Duration.ofMinutes(5));

    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }
}