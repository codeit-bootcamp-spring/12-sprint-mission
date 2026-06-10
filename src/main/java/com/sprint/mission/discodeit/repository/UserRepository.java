package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.user.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Override
    @EntityGraph(attributePaths = {"profile", "status"})
    Optional<User> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"profile", "status"})
    List<User> findAll();

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}