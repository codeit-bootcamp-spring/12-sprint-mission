package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(

        @NotBlank(message = "채널 이름은 필수입니다.")
        @Size(min = 2, max = 50, message = "채널 이름은 2글자 이상 50글자 미만입니다.")
        String name,

        @Max(value = 200, message = "채널 설명은 200글자 미만입니다.")
        String description

) {

}
