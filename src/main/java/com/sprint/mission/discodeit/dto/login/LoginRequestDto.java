package com.sprint.mission.discodeit.dto.login;

import java.util.UUID;

public record LoginRequestDto(
        String username,
        String password
) {
}
