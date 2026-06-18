package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record PrivateChannelCreateRequest(
	@NotNull
    List<UUID> participantIds
) {

}
