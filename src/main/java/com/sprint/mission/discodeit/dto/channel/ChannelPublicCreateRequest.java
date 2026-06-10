package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;

public record ChannelPublicCreateRequest(
    @NotBlank(message = "채널명은 필수입니다.")
    String name,
    String description
) {

}
