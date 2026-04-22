package com.sprint.mission.discodeit.data.request.message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record MessageCreateRequest(
        UUID authorId,
        UUID channelId,
        String content,
        Optional<List<BinaryContentCreateRequest>> binarRequestList
) {
    public MessageCreateRequest {
        if (authorId == null) {
            throw new IllegalArgumentException("누가 메시지를 만들었니?");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("채널 없음");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메세지 내용 없음");
        }
    }
}