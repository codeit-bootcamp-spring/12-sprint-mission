package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends UpdatableBaseEntity {

  @Column(length = 100)
  private String name;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private ChannelType type;
  @Column(length = 500)
  private String description;

  private Channel(String name, ChannelType type, String description) {
    super();
    this.name = name;
    this.type = type;
    this.description = description;
  }

  public static Channel createPublic(String name, String description) {
    return new Channel(name, ChannelType.PUBLIC, description);
  }

  public static Channel createPrivate(String name) {
    return new Channel(name, ChannelType.PRIVATE, null);
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateChannelType(ChannelType type) {
    this.type = type;
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  public void changeChannel(String name, String description) {
    if (name != null && !name.isBlank()) {
      this.name = name;
    }
    if (description != null && !description.isBlank()) {
      this.description = description;
    }
  }

}
