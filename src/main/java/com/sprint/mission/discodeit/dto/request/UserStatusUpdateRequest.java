package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record UserStatusUpdateRequest(

        @NotBlank(message = "마지막 활동 시간은 필수입니다.")
        @PastOrPresent(message = "마지막 활동 시간은 현재 또는 과거여야합니다.")
        Instant newLastActiveAt
) {

}
