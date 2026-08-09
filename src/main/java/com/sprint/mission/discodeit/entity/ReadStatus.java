package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
// user_id + channel_id 조합 유니크 제약 (한 유저가 같은 채널에 중복 읽음 상태 불가)
@Table(name = "read_statuses",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"}))
public class ReadStatus extends BaseUpdatableEntity {

  // N:1 - 유저 삭제 시 ReadStatus도 삭제 (ON DELETE CASCADE)
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // N:1 - 채널 삭제 시 ReadStatus도 삭제 (ON DELETE CASCADE)
  @ManyToOne
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(nullable = false)
  private Instant lastReadAt;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    super();
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
  }

  public void update(Instant newLastReadAt) {
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
      this.updatedAt = Instant.now();
    }
  }
}
