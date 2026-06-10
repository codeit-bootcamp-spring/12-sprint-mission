package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreatePrivateChannelRequest(
        @NotEmpty(message = "PRIVATE 채널 참여자는 최소 1명 이상이어야 합니다.")
        List<@NotNull(message = "참여자 ID는 null일 수 없습니다.") UUID> participantIds
) {
}