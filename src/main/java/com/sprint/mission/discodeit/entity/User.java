package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  private UUID profileId;

  protected User() {

  }

  public User(String username, String email,
              String password, UUID profileId) {

    this.username = username;
    this.email = email;
    this.password = password;
    this.profileId = profileId;
  }

  public void update(String newUsername,
                     String newEmail,
                     String newPassword,
                     UUID newProfileId) {

    if (newUsername != null &&
            !newUsername.equals(this.username)) {
      this.username = newUsername;
    }

    if (newEmail != null &&
            !newEmail.equals(this.email)) {
      this.email = newEmail;
    }

    if (newPassword != null &&
            !newPassword.equals(this.password)) {
      this.password = newPassword;
    }

    if (newProfileId != null &&
            !newProfileId.equals(this.profileId)) {
      this.profileId = newProfileId;
    }
  }
}