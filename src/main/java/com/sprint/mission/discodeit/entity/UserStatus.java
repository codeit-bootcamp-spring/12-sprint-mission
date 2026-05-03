package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.models.security.SecurityScheme.In;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;
  private UUID userId;

  private Instant createdAt;
  private Instant updatedAt;
  private Instant lastActiveAt;

  public UserStatus(UUID userId, Instant lastActiveAt) {
    this.id = UUID.randomUUID();
    this.userId = userId;

    this.createdAt = Instant.now();
    this.lastActiveAt = lastActiveAt;
  }

  public void updateTimestamp(Instant lastActiveAt) {
    boolean anyValueUpdated = false;
    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
      anyValueUpdated = true;
      this.lastActiveAt = lastActiveAt;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

  public boolean isUserOnline() {
    Instant threshold = Instant.now().minus(5, ChronoUnit.MINUTES);
    return lastActiveAt.isAfter(threshold);
  }
}
