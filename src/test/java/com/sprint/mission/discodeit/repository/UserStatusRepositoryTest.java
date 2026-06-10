package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
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
class UserStatusRepositoryTest {

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Autowired
  private TestEntityManager em;

  private User user1;
  private User user2;
  private UserStatus userStatus;

  @BeforeEach
  void setUp() {
    user1 = new User("user1", "user1@example.com", "password1", null);
    em.persist(user1);

    user2 = new User("user2", "user2@example.com", "password2", null);
    em.persist(user2);

    userStatus = new UserStatus(user1, Instant.now());
    em.persist(userStatus);

    em.flush();
    em.clear();
  }

  // ── findByUserId ─────────────────────────────────────────

  @Test
  @DisplayName("userId로 UserStatus 조회 성공")
  void findByUserId_Success() {
    // when
    Optional<UserStatus> result = userStatusRepository.findByUserId(user1.getId());

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getUser().getId()).isEqualTo(user1.getId());
  }

  @Test
  @DisplayName("UserStatus 없는 userId 조회 시 empty 반환")
  void findByUserId_NotFound_ReturnsEmpty() {
    // when
    Optional<UserStatus> result = userStatusRepository.findByUserId(user2.getId());

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("존재하지 않는 userId 조회 시 empty 반환")
  void findByUserId_NonExistentUser_ReturnsEmpty() {
    // when
    Optional<UserStatus> result = userStatusRepository.findByUserId(UUID.randomUUID());

    // then
    assertThat(result).isEmpty();
  }
}