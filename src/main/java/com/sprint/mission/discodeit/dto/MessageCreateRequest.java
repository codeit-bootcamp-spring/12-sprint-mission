package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID userId,
        UUID channelId,
        List<BinaryContentCreateRequest> attachments // 이거 사진 이미지?
) {
    public Message toMessage() {
        return Message.builder()
                .content(content)
                .userId(userId)
                .channelId(channelId)
                .build();
    }
}