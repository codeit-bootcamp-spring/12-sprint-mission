package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class BinaryContentCreateDTO {
    private String contentType;
    private byte[] content;
}
