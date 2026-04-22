package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageCreateRequest {
    private final String content;
    private final UUID channelId;
    private final UUID authorId;

    @Nullable
    private final List<BinaryContentCreateRequest> attachments;
}

