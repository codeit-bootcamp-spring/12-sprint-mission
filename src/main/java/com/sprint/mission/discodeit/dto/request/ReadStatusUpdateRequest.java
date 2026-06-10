package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record ReadStatusUpdateRequest(

        @NotNull(message = "마지막 문자 확인 시간은 필수입니다.")
        @PastOrPresent(message="마지막 확인 시간은 현재 또는 과거여야합니다.")
        Instant newLastReadAt
) {

}
