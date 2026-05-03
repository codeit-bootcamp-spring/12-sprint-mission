package com.sprint.mission.discodeit.dto.request;

import java.util.Objects;
import lombok.Getter;

@Getter
public final class UserCreateRequest {

  private final String username;
  private final String email;
  private final String password;

  public UserCreateRequest(
      String username,
      String email,
      String password
  ) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public String username() {
    return username;
  }

  public String email() {
    return email;
  }

  public String password() {
    return password;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (UserCreateRequest) obj;
    return Objects.equals(this.username, that.username) &&
        Objects.equals(this.email, that.email) &&
        Objects.equals(this.password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, email, password);
  }

  @Override
  public String toString() {
    return "UserCreateRequest[" +
        "username=" + username + ", " +
        "email=" + email + ", " +
        "password=" + password + ']';
  }

}
