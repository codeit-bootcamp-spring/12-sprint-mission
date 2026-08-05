package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  @EntityGraph(attributePaths = {"profile"})
  Optional<User> findByUsername(String username);

  @Query("""
      select u
      from User u
      """)
  @EntityGraph(attributePaths = {"profile"})
  List<User> findAllWithFetch();

  @Query("""
      select u
      from User u
      where u.id = :userId
      """)
  @EntityGraph(attributePaths = {"profile"})
  Optional<User> findDetailById(UUID userId);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByRole(Role role);
}
