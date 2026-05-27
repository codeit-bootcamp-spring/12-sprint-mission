package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

  @Size(max = 100)
  @Column(name = "name", length = 100)
  private String name;
  @Size(max = 500)
  @Column(name = "description", length = 500)
  private String description;
  @Size(max = 10)
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 10)
  private ChannelType type;
  @NotNull
  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Message> messages = new ArrayList<>();
  @NotNull
  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReadStatus> readStatuses = new ArrayList<>();

  public Channel(ChannelType type, String name, String description) {
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void update(String newName, String newDescription) {
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
    }
  }
}
