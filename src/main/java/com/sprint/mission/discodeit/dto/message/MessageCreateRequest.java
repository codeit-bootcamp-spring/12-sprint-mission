package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record MessageCreateRequest(
    @NotNull(message = "사용자 ID는 필수입니다.")
    UUID userId,

    @NotNull(message = "채널 ID는 필수입니다.")
    UUID channelId,

    @NotBlank(message = "메시지 내용은 필수입니다.")
    String content
) {


}
