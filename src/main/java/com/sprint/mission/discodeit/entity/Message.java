package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "messages")
@Getter
@Setter
@ToString(callSuper = true, exclude = {"channel", "author", "attachments"})
@SuperBuilder
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

  @Column(name = "content")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private User author;

  //  정책상 공유가 아니라 개개인의 binary_content를 사용하니까
  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "attachment_id", nullable = false)
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
    if (content == null) {
      throw new IllegalArgumentException("content is null.");
    }
    if (channel == null) {
      throw new IllegalArgumentException("channel is null.");
    }
    if (author == null) {
      throw new IllegalArgumentException("author is null.");
    }
    if (attachments == null) {
      throw new IllegalArgumentException("attachments is null.");
    }

    this.channel = channel;
    this.author = author;
    this.attachments = new ArrayList<>(List.copyOf(attachments));
    this.content = content;
  }

  //  orphanRemoval = true이기 때문에 clear할 경우 BinaryContent row가 DB에서 삭제.
//  실제 파일 삭제는 서비스 계층에서 별도로 처리중.
//  현재는 BinaryContent는 메타데이터 관리, Storage는 파일을 관리.
//  추후 로직을 정리할 경우 BinaryContent와 Storage 부분을 참고하여 수정 필요.
  public void updateAttachments(List<BinaryContent> attachments) {
    if (attachments == null) {
      throw new IllegalArgumentException("attachments is null.");
    }

    this.attachments.clear();
    this.attachments.addAll(attachments);
  }
}
