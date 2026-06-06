package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @Size(max = 100, message = "채널 이름은 100자 이하로 입력해주세요.")
    String newName,

    @Size(max = 500, message = "채널 설명은 500자 이하로 입력해주세요.")
    String newDescription
) {

}