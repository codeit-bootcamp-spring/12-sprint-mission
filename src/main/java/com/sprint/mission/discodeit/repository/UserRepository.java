package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  // N+1 문제 해결: User 조회 시 profile(@OneToOne), status(@OneToOne)를 JOIN으로 한 번에 로딩
  // @EntityGraph 없이 findAll() 하면 각 User마다 profile/status를 개별 SELECT → N+1 발생
  @EntityGraph(attributePaths = {"profile", "status"})
  List<User> findAll();

  @EntityGraph(attributePaths = {"profile", "status"})
  Optional<User> findById(UUID id);

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
