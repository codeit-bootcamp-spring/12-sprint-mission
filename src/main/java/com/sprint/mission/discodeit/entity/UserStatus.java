package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.Instant;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_statuses")
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(nullable = false)
  private Instant lastActiveAt;

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public void update(Instant newLastActiveAt) {
    if (newLastActiveAt != null) {
      this.lastActiveAt = newLastActiveAt;
    }
  }
}
