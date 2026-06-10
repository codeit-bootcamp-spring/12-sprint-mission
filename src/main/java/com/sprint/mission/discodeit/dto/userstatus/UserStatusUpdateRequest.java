package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public record UserStatusUpdateRequest(
    @NotBlank(message = "마지막 접속 시간은 필수입니다.")
    Instant newLastActiveAt
) {

}
