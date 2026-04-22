package com.sprint.mission.discodeit.dto.login;

import java.util.UUID;

public record LoginResponseDto(
        UUID id,
        String username,
        String email
) {
}
