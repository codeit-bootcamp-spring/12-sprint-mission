package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @NotNull(message = "참여자 목록은 필수입니다.")
    @Size(min = 1, message = "참여자는 1명 이상이어야 합니다.")
    List<@NotNull(message = "참여자 ID는 null일 수 없습니다.") UUID> participantIds
) {

}