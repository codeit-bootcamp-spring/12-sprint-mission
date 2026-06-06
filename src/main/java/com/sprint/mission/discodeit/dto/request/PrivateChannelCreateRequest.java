package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotBlank(message = "채널명은 필수입니다.")
    String name,

    @NotNull(message = "채널 타입은 필수입니다.")
    @Pattern(regexp = "PRIVATE", message = "채널 타입은 PRIVATE 여야 합니다.")
    String type  // "PRIVATE"
) {

}