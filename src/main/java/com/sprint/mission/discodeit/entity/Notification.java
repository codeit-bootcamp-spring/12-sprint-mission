package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 알림은 생성 후 '확인(삭제)'만 가능하므로 수정 시점이 없다 → BaseEntity
@Getter
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

  // N:1 - 유저 삭제 시 알림도 삭제 (ON DELETE CASCADE)
  @ManyToOne
  @JoinColumn(name = "receiver_id", nullable = false)
  private User receiver;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, length = 500)
  private String content;

  public Notification(User receiver, String title, String content) {
    this.receiver = receiver;
    this.title = title;
    this.content = content;
  }
}
