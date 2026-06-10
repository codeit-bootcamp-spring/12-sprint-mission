package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundException extends UserException {

    public UserStatusNotFoundException(UUID userStatusId) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of("userStatusId", userStatusId));
    }

    public static UserStatusNotFoundException byUserId(UUID userId) {
        return new UserStatusNotFoundException("userId", userId);
    }

    private UserStatusNotFoundException(String field, UUID value) {
        super(ErrorCode.USER_STATUS_NOT_FOUND, Map.of(field, value));
    }
}