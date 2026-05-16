package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.AccessLevel;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

  private String username;
  private String email;
  private String password;

  private BinaryContent profile;

  private UserStatus userStatus;

  public User(String username, String email, String password, BinaryContent profile) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  public void update(
      String newUsername, String newEmail, String newPassword, BinaryContent newProfile
  ) {
    if (newUsername != null) {
      this.username = newUsername;
    }

    if (newEmail != null) {
      this.email = newEmail;
    }
    if (newPassword != null) {
      this.password = newPassword;
    }

    if (newProfile != null) {
      this.profile = newProfile;
    }

  }
}
