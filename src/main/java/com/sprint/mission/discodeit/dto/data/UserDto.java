package com.sprint.mission.discodeit.dto.data;

import org.springframework.context.annotation.Role;

import java.util.UUID;
import java.util.prefs.Preferences;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Boolean online,
    Role role
) {
}


