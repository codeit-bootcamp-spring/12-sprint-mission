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
  private String username;
  private String email;
  private String password;
  private UUID profileId;     // BinaryContent

  public User() {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
  }

  public User(String username, String email, String password) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public User(String username, String email, String password, UUID nullableProfileId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.username = username;
    this.email = email;
    this.password = password;
    this.profileId = nullableProfileId;
  }

  public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
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
    if (newProfileId != null && !newProfileId.equals(this.profileId)) {
      this.profileId = newProfileId;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }

  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setProfileId(UUID profileId) {
    this.profileId = profileId;
  }
}



