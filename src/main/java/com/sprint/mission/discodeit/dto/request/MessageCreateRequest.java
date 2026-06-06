package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotNull(message = "채널 ID는 필수입니다.")
    Long channelId,

    @NotNull(message = "작성자 ID는 필수입니다.")
    Long userId,

    @NotBlank(message = "메시지 내용은 비어 있을 수 없습니다.")
    String content
) {

}