package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    // bytes는 다운로드 시에만 사용, 일반 조회 시 null
    byte[] bytes
) {

}
