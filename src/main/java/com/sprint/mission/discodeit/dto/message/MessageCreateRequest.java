package com.sprint.mission.discodeit.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreateRequest {
    private String content;
    private UUID userId;
    private UUID channelId;

    // 선택적 첨부파일 여러 개
    private List<UUID> binaryContentIds;
}