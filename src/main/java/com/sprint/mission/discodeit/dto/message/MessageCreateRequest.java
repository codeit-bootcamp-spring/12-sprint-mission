package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "메시지를 작성해주세요.")
    String content,
    @NotNull(message = "채널 ID는 필수입니다.")
    UUID channelId,
    @NotNull(message = "작성자 ID는 필수입니다.")
    UUID authorId
) {

}
