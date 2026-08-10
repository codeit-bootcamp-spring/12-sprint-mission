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

  // 채널 별 알림 수신 여부
  @Column(nullable = false)
  private boolean notificationEnabled;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    super();
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
    // PRIVATE 채널은 대화 상대가 정해져 있으므로 알림을 켜고, PUBLIC 채널은 소음이 크므로 끈다.
    // ReadStatus 생성 경로(채널 생성/채널 최초 진입)가 여러 개라 생성자에서 한 번에 정한다.
    this.notificationEnabled = channel.getType() == ChannelType.PRIVATE;
  }

  public void update(Instant newLastReadAt, Boolean newNotificationEnabled) {
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
      this.updatedAt = Instant.now();
    }
    // 프론트엔드는 읽음 시각과 알림 설정을 각각 따로 보내므로 null은 '변경 없음'으로 취급한다
    if (newNotificationEnabled != null && newNotificationEnabled != this.notificationEnabled) {
      this.notificationEnabled = newNotificationEnabled;
      this.updatedAt = Instant.now();
    }
  }
}
