package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.BatchSize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

  @Column(columnDefinition = "text")
  private String content;

  // N:1 - 채널 삭제 시 메시지도 삭제 (ON DELETE CASCADE)
  @ManyToOne
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  // N:1 - 유저 삭제 시 author_id null (ON DELETE SET NULL)
  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author;

  // N:N - message_attachments 조인 테이블 사용, 메시지 삭제 시 첨부파일도 삭제
  // @BatchSize: 페이지네이션 쿼리에서 fetch join 대신 IN절 배치 조회로 메모리 페이징 방지
  @BatchSize(size = 100)
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "attachment_id")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
    super();
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachments = attachments != null ? attachments : new ArrayList<>();
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
      this.updatedAt = Instant.now();
    }
  }
}
