package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일 이름은 비어 있을 수 없습니다.")
    String fileName,
    @NotBlank(message = "Content-Type은 비어 있을 수 없습니다.")
    String contentType,
    @NotEmpty(message = "파일 데이터는 비어 있을 수 없습니다.")
    @Size(max = 10 * 1024 * 1024) // 최대 10MB
    byte[] bytes
) {

}
