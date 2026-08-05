package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import java.time.Instant;

public record JwtInformation(
    UserResponse userResponse,
    String accessToken,
    String refreshToken,
    Instant expiresAt
) {
}
