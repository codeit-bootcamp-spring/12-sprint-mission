package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(
    @NotBlank(message = "메시지 내용은 공백이 될 수 없습니다.")
    String newContent
) {

}
