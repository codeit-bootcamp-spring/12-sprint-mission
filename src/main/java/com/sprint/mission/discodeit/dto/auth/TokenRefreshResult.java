package com.sprint.mission.discodeit.dto.auth;

public record TokenRefreshResult(
    JwtDto jwtDto,
    String refreshToken,
    long refreshTokenExpiration
) {

}
