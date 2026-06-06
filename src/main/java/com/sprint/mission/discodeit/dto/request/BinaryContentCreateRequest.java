package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(

        @NotBlank(message = "파일 이름은 필수입니다.")
        @Size(max = 255, message = "파일 이름은 255자를 초과할 수 없습니다.")
        String fileName,

    @NotBlank(message = "콘텐츠 유형은 필수입니다.")
    @Size(max = 100, message = "콘텐츠 유형은 100자를 초과할 수 없습니다.")
    String contentType,

    @NotEmpty(message = "파일 내용은 필수입니다.")
    @Size(max = 10 * 1024 * 1024, message = "파일 크기는 10MB를 초과할 수 없습니다.")
    byte[] bytes
) {

}
