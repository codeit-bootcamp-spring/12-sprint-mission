package com.sprint.mission.discodeit.dto.binarycontent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record BinaryContentCreateRequest(
        @NotEmpty(message = "파일 데이터는 필수입니다.")
        byte[] data,

        @NotBlank(message = "파일 이름은 필수입니다.")
        String fileName,

        @NotBlank(message = "파일 형식은 필수입니다.")
        String contentType
) {
}