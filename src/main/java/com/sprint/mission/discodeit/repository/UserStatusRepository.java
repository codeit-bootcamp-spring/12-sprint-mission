package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.user.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

   @EntityGraph(attributePaths = {"user", "user.profile"})
   Optional<UserStatus> findByUser_Id(UUID userId);

   @Override
   @EntityGraph(attributePaths = {"user", "user.profile"})
   List<UserStatus> findAll();
}