package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(callSuper = true, exclude = {"profile", "readStatuses",})
@SuperBuilder
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  @Default
  private Role role = Role.USER;

  //  정책상 프로필 이미지만 쓰는 것 같은데 orphan 가능할듯
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "profile_id", unique = true)
  private BinaryContent profile;

  @OneToMany(mappedBy = "user")
  private List<ReadStatus> readStatuses = new ArrayList<>();

  public User(String username, String email, String password, BinaryContent profile) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("username is blank.");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email is blank.");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("password is blank.");
    }

    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
    this.role = Role.USER;
  }

  public void updateRole(Role role) {
    if (role == null) {
      throw new IllegalArgumentException("role is null.");
    }
    this.role = role;
  }
}