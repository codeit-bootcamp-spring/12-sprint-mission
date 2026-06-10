package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public record ChannelPrivateCreateRequest(
    @NotBlank(message = "채널 이름은 필수입니다.")
    String name,
    List<UUID> participantIds
) {

}
