package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record MessageUpdateRequestDto(
	UUID messageId,
	String newContent
) {
}
