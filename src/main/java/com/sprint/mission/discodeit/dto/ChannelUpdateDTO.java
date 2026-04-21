package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ChannelUpdateDTO {
    private UUID id;
    private String name;
    private String description;
}
