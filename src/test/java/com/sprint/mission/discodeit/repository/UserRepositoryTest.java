package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager em;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User("testUser", "test@example.com", "password123", null);
    em.persist(user);

    UserStatus status = new UserStatus(user, Instant.now());
    em.persist(status);

    em.flush();
    em.clear();
  }

  // ── findByUsername ───────────────────────────────────────

  @Test
  @DisplayName("username으로 사용자 조회 성공")
  void findByUsername_Success() {
    // when
    Optional<User> result = userRepository.findByUsername("testUser");

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("존재하지 않는 username 조회 시 빈 Optional 반환")
  void findByUsername_NotFound_ReturnsEmpty() {
    // when
    Optional<User> result = userRepository.findByUsername("없는유저");

    // then
    assertThat(result).isEmpty();
  }

  // ── existsByEmail ────────────────────────────────────────

  @Test
  @DisplayName("존재하는 이메일이면 true 반환")
  void existsByEmail_Exists_ReturnsTrue() {
    // when
    boolean result = userRepository.existsByEmail("test@example.com");

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 이메일이면 false 반환")
  void existsByEmail_NotExists_ReturnsFalse() {
    // when
    boolean result = userRepository.existsByEmail("none@example.com");

    // then
    assertThat(result).isFalse();
  }

  // ── existsByUsername ─────────────────────────────────────

  @Test
  @DisplayName("존재하는 username이면 true 반환")
  void existsByUsername_Exists_ReturnsTrue() {
    // when
    boolean result = userRepository.existsByUsername("testUser");

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 username이면 false 반환")
  void existsByUsername_NotExists_ReturnsFalse() {
    // when
    boolean result = userRepository.existsByUsername("없는유저");

    // then
    assertThat(result).isFalse();
  }

  // ── findAllWithProfileAndStatus ──────────────────────────

  @Test
  @DisplayName("프로필과 상태를 포함한 전체 사용자 조회 성공")
  void findAllWithProfileAndStatus_Success() {
    // when
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUsername()).isEqualTo("testUser");
    assertThat(result.get(0).getStatus()).isNotNull();
  }

  @Test
  @DisplayName("UserStatus 없는 사용자는 조회 안됨")
  void findAllWithProfileAndStatus_WithoutStatus_NotIncluded() {
    // given
    User userWithoutStatus = new User("noStatus", "nostatus@example.com", "password123", null);
    em.persist(userWithoutStatus);
    em.flush();
    em.clear();

    // when
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(result).hasSize(1);
    assertThat(result).noneMatch(u -> u.getUsername().equals("noStatus"));
  }
}