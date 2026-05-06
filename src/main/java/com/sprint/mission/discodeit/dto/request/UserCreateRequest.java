package com.sprint.mission.discodeit.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserCreateRequest(
    String username,
    String email,
    String password
) {

  @JsonCreator
  public UserCreateRequest(
      @JsonProperty("username") String username,
      @JsonProperty("email") String email,
      @JsonProperty("password") String password
  ) {
    this.username = username;
    this.email = email;
    this.password = password;
  }
}