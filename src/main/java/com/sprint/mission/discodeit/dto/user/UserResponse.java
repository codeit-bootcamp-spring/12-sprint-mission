package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    BinaryContentResponse profile,
    Boolean online
) {

}


