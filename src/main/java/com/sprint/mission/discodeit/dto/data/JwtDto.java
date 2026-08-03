package com.sprint.mission.discodeit.dto.data;

/**
 * 로그인/토큰 재발급 응답.
 *
 * <p>리프레시 토큰은 XSS로 탈취되지 않도록 HttpOnly 쿠키로만 내려주고, 응답 Body에는 포함하지 않는다.
 */
public record JwtDto(
    UserDto userDto,
    String accessToken
) {

}
