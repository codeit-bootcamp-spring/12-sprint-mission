package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MessageCreateDTO {
    private String content;
    private UUID channelId;
    private UUID authorId;
    private List<String> attachmentTypes;
    private List<byte[]> attachments;
}
