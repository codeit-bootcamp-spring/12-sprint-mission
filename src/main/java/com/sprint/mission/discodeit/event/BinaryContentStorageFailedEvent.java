package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record BinaryContentStorageFailedEvent(
    String taskName,
    String requestId,
    UUID binaryContentId,
    String errorMessage
) {

}