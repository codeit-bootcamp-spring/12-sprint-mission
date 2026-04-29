package com.sprint.mission.discodeit.dto.request;

import java.util.List;

public record MessageCreateApiRequest(
        MessageCreateRequest message,
        List<BinaryContentCreateRequest> attachments
) {
}
