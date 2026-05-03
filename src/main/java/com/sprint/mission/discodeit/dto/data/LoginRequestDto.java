package com.sprint.mission.discodeit.dto.data;

import lombok.Getter;

@Getter
public class LoginRequestDto {

  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public LoginRequestDto() {
  }

}

