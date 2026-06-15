package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널명은 필수입니다.")
    String name,

    @NotNull(message = "채널 타입은 필수입니다.")
    @Pattern(regexp = "PUBLIC", message = "채널 타입은 PUBLIC 여야 합니다.")
    String type  // "PUBLIC"
) {

}