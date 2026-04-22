package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.user.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
   UserStatus save(UserStatus userStatus);
   Optional<UserStatus> findById(UUID id);
   List<UserStatus> findAll();
   Optional<UserStatus> findByUserId(UUID userId);
   boolean existsById(UUID id);
   void deleteById(UUID id);
}
