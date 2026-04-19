package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BinaryContentCreateRequest {
    private String fileName;
    private String contentType;
    private byte[] data;


    private UUID userId;

    private UUID messageId;
}