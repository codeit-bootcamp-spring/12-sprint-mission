package com.sprint.mission.discodeit.dto.message;

import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public record MessageUpdateRequest(
    UUID id,
    String content
) {

}
