package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicChannelCreateRequest(
	@NotBlank
    String name,
	@NotNull
    String description
) {

}
