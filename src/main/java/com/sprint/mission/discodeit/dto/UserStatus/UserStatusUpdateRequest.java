package com.sprint.mission.discodeit.dto.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID id, // 그룹화 때문에 있어야하는지 없어야하는지 잘 모르겠습니다.
        Instant lastSeenAt
) {
}
