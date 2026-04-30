package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Schema(description = "수정할 Message 내용")
public record MessageUpdateRequest(
    String newContent
) {

}
