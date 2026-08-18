package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;

// 두 필드는 각각 독립적으로 수정된다. null은 '변경하지 않음'을 의미한다.
public record ReadStatusUpdateRequest(
    Instant newLastReadAt,
    Boolean newNotificationEnabled
) {

}
