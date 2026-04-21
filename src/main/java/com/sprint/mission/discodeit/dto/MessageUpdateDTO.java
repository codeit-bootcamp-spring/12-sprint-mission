package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MessageUpdateDTO {
    private UUID id;
    private String content;
    private List<String> attachmentTypes;
    private List<byte[]> attachments;
}
