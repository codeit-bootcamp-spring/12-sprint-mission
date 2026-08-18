package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    // 프론트엔드가 이 값을 폴링해 업로드 진행/실패를 표시한다
    BinaryContentStatus status,
    // bytes는 다운로드 시에만 사용, 일반 조회 시 null
    byte[] bytes
) {

}
