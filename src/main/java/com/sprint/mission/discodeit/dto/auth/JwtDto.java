package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.dto.user.UserResponse;

public record JwtDto(
    UserResponse userResponse,
    String accessToken
) {

}
