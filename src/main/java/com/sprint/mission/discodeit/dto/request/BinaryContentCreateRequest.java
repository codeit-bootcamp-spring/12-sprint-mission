package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BinaryContentCreateRequest {
    private final String fileName;
    private final String contentType;
    private final byte[] bytes;
}
