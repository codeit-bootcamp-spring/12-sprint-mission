package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(

        @NotEmpty(message = "참여자 목록은 필수입니다.")
        @Size(min=2 , message = "비공개 채널은 최소 2명 이상의 참여자가 필요합니다.")
        List<@NotNull(message = "참여자 Id는 null일수 없습니다.")UUID> participantIds
) {

}
