package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record MessageData(UUID authorId, UUID channelId, String content) {
    public MessageData {
        if (authorId == null) {
            throw new IllegalArgumentException("누가 메시지를 만들었니?");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("채널 없음");
        }

        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("메세지 내용 없음");
        }
    }
}