package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "channels")
public class Channel extends BaseUpdatableEntity {

  // @Enumerated(STRING): DB에 "PUBLIC"/"PRIVATE" 문자열로 저장
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private ChannelType type;

  @Column(length = 100)
  private String name;

  @Column(length = 500)
  private String description;

  public Channel(ChannelType type, String name, String description) {
    super();
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void update(String newName, String newDescription) {
    boolean anyValueUpdated = false;
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
      anyValueUpdated = true;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
      anyValueUpdated = true;
    }
    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
