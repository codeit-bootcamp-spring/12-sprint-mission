package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private ChannelType type;
  @Column(length = 100)
  private String name;
  @Column(length = 500)
  private String description;

  public Channel(ChannelType type, String name, String description) {
    if (type == ChannelType.PRIVATE && (name != null || description != null)) {
      throw new IllegalArgumentException("PRIVATE 채널은 name/description을 가질 수 없습니다.");
    }
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void update(String newName, String newDescription) {
    if (newName != null && !newName.isBlank() && !newName.equals(this.name)) {
      if (newDescription != null && !newDescription.equals(this.description)) {
      }
    }
  }
}