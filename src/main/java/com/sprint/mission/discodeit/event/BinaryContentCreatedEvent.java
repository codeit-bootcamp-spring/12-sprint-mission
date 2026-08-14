package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    BinaryContentCreateRequest binaryContentCreateRequest
) {

}