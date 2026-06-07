package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank
    @Size(max = 2000)
    String content,

    @NotNull
    UUID channelId,

    @NotNull
    UUID authorId
) {

}