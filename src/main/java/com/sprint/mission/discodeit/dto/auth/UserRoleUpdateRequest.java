package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserRoleUpdateRequest(
    @NotNull UUID userId,
    @NotNull UserRole role
) {
}
