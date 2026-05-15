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
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.NonNull;

@Entity
@Getter
@SuperBuilder
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity implements Comparable<Message> {

  @Column(name = "content", length = Integer.MAX_VALUE)
  private String content;
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "channel_id")
  private Channel channel;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private User author;
  @NonNull
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = "message_attachments",
      joinColumns = {@JoinColumn(name = "message_id")},
      inverseJoinColumns = {@JoinColumn(name = "attachment_id")})
  private List<BinaryContent> attachments = new ArrayList<>();


  public void setBinaryContents(@NonNull List<BinaryContent> attachments) {
    this.attachments = attachments;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
  }

  @Override
  public int compareTo(Message o) {
    return this.getCreatedAt().compareTo(o.getCreatedAt());
  }
}
