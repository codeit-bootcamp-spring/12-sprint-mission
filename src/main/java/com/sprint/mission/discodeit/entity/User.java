package com.sprint.mission.discodeit.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends UpdatableBaseEntity {

  @Column(unique = true, nullable = false, length = 100)
  private String email;
  @Column(unique = true, nullable = false, length = 50)
  private String username;
  @Column(nullable = false, length = 60)
  private String password;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id", unique = true)
  private BinaryContent profile;

  @OneToOne(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY
  )
  private UserStatus status;

  public User(String email, String username, String password, BinaryContent profile) {
    this(email, username, password, UserRole.USER, profile);
  }

  public User(
      String email,
      String username,
      String password,
      UserRole role,
      BinaryContent profile
  ) {
    super();
    this.email = email;
    this.username = username;
    this.password = password;
    this.role = role;
    this.profile = profile;
  }

  public void initStatus() {
    this.status = new UserStatus(this);
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updateUsername(String username) {
    this.username = username;
  }

  public void changeRole(UserRole role) {
    this.role = role;
  }

  public void changeProfile(String email, String username, BinaryContent profile) {
    if (email != null && !email.isBlank()) {
      this.email = email;
    }

    if (username != null && !username.isBlank()) {
      this.username = username;
    }

    if (profile != null) {
      this.profile = profile;
    }
  }

}
