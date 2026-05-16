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
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "channels")
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String name;

  @Column
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChannelType type;

  public Channel(String name, String description, ChannelType type) {
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void update(String newName, String newDescription, ChannelType newType) {
    if (newName != null) {
      this.name = newName;
    }

    if (newDescription != null) {
      this.description = newDescription;
    }

    if (newType != null) {
      this.type = newType;
    }
  }
}
