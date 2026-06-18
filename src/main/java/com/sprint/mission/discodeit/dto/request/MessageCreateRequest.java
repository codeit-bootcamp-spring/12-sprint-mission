package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageCreateRequest(
	@NotBlank
    String content,
	@NotNull
    UUID channelId,
	@NotNull
    UUID authorId
) {

}