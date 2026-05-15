package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

  private String content;

  private UUID channelId;
  private UUID authorId;
  private List<UUID> attachmentIds;

  protected Message() {
  }

  public Message(String content,
                 UUID channelId,
                 UUID authorId,
                 List<UUID> attachmentIds) {

    this.content = content;
    this.channelId = channelId;
    this.authorId = authorId;
    this.attachmentIds = attachmentIds;
  }

  public void update(String newContent) {

    if (newContent != null &&
            !newContent.equals(this.content)) {

      this.content = newContent;
    }
  }
}