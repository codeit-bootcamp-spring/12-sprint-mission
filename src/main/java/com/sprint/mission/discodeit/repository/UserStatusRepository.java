package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus create(UserStatus userStatus);
    Optional<UserStatus> findById(UUID id);
    UserStatus update(UUID id, UserStatus userStatus);
    boolean delete(UUID id);
}
