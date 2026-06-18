package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.PastOrPresent;

public record ReadStatusUpdateRequest(
	@PastOrPresent
    Instant newLastReadAt
) {

}
