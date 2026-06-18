package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record UserStatusCreateRequest(
	@NotNull
    UUID userId,
	@PastOrPresent
    Instant lastActiveAt
) {

}
