package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;
  //
  private String username;
  private String email;
  private String password;

  private UUID profileId;

  public User(String username, String email, String password, UUID profileImageId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.username = username;
    this.email = email;
    this.password = password;
    this.profileId = profileImageId;
  }

  // 호환성 위해 남겨둠 삭제해도될듯
//    public User(String newUsername, String newEmail, String newPassword) {
//        this(newUsername ,newEmail, newPassword, null);
//    }

  // 22
//    public void update(String newUsername, String newEmail, String newPassword) {
//        update(newUsername, newEmail, newPassword, null);
//    }

  public void update(String newUsername, String newEmail, String newPassword, UUID profileImageId) {
    boolean anyValueUpdated = false;
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
      anyValueUpdated = true;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
      anyValueUpdated = true;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
      anyValueUpdated = true;
    }

    if (profileImageId != null && !profileImageId.equals(this.profileId)) {
      this.profileId = profileImageId;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}
