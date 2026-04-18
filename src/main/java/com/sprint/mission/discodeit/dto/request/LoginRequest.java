package com.sprint.mission.discodeit.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    private final String username;
    private final String password;
}
