package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import lombok.Getter;

@Getter
public class JwtInformation {

    private final UserResponse userDto;
    private String accessToken;
    private String refreshToken;

    public JwtInformation(UserResponse userDto, String accessToken, String refreshToken) {
        this.userDto = userDto;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void rotate(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
