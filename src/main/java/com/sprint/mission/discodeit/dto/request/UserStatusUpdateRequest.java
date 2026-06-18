package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.PastOrPresent;

public record UserStatusUpdateRequest(
	@PastOrPresent
    Instant newLastActiveAt
) {

}
