package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false, length = 60)
  private String password;

  // 문자열로 저장해야 순서가 바뀌어도 안전하므로 EnumType.STRING 사용
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role;

  // 1:1 단방향 - profile 삭제 시 profileId null (ON DELETE SET NULL)
  @OneToOne
  @JoinColumn(name = "profile_id", unique = true)
  private BinaryContent profile;

  // 회원가입으로 만들어지는 사용자는 항상 USER 권한
  public User(String username, String email, String password, BinaryContent profile) {
    this(username, email, password, profile, Role.USER);
  }

  public User(String username, String email, String password, BinaryContent profile, Role role) {
    super();
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
    this.role = role;
  }

  public void update(String newUsername, String newEmail, String newPassword,
      BinaryContent newProfile) {
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
    if (newProfile != null) {
      this.profile = newProfile;
      anyValueUpdated = true;
    }
    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

  public void updateRole(Role newRole) {
    if (newRole != null && newRole != this.role) {
      this.role = newRole;
      this.updatedAt = Instant.now();
    }
  }
}
