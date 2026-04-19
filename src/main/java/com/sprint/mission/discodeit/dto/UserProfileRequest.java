package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserProfileRequest(
        UUID id,
        UUID profileId
) {
}
