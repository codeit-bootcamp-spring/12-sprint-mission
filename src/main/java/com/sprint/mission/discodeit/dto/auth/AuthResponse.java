package com.sprint.mission.discodeit.dto.auth;

import java.util.UUID;

public record AuthResponse(
        UUID id,
        String username,
        String email,
        UUID profileId
) {}
