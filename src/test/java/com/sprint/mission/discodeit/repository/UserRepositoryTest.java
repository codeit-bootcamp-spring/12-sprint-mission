package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Role;
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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@EnableJpaAuditing
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {


  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserStatusRepository userStatusRepository;

  private User savedUser;

  @BeforeEach
  void setUp() {
    savedUser = userRepository.save(new User("JaneDoe", "JaneDoe@codeit.com", "password123", null, Role.ADMIN));
    userStatusRepository.save(new UserStatus(savedUser, Instant.now()));
  }

  @Test
  @DisplayName("유저명으로 조회 성공")
  void findByUsername_success() {
    // when
    Optional<User> result = userRepository.findByUsername("JaneDoe");

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("JaneDoe@codeit.com");
  }

  @Test
  @DisplayName("유저명으로 조회 실패 - 존재하지 않는 유저")
  void findByUsername_notFound() {
    // when
    Optional<User> result = userRepository.findByUsername("없는유저");

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("이메일 존재 여부 확인 - 존재하는 경우")
  void existsByEmail_true() {
    // when
    boolean exists = userRepository.existsByEmail("JaneDoe@codeit.com");

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("이메일 존재 여부 확인 - 존재하지 않는 경우")
  void existsByEmail_false() {
    // when
    boolean exists = userRepository.existsByEmail("none@codeit.com");

    // then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("유저명 존재 여부 확인 - 존재하는 경우")
  void existsByUsername_true() {
    // when
    boolean exists = userRepository.existsByUsername("JaneDoe");

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("유저명 존재 여부 확인 - 존재하지 않는 경우")
  void existsByUsername_false() {
    // when
    boolean exists = userRepository.existsByUsername("없는유저");

    // then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("프로필과 상태 포함 전체 조회 - JOIN FETCH")
  void findAllWithProfileAndStatus_success() {
    // given - 추가 유저 저장
    User user2 = userRepository.save(new User("JohnDoe", "JohnDoe@codeit.com", "pw", null, Role.ADMIN));
    userStatusRepository.save(new UserStatus(user2, Instant.now()));

    // when
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(result).hasSize(2);
    assertThat(result).extracting(User::getUsername)
        .containsExactlyInAnyOrder("JaneDoe", "JohnDoe");
  }

  @Test
  @DisplayName("프로필과 상태 포함 전체 조회 - UserStatus 없는 유저는 제외")
  void findAllWithProfileAndStatus_excludeWithoutStatus() {
    // given - UserStatus 없는 유저 저장
    userRepository.save(new User("상태없음", "nostatus@codeit.com", "pw", null, Role.ADMIN));

    // when
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then
    // JOIN FETCH이므로 status 없는 유저는 제외됨
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUsername()).isEqualTo("JaneDoe");
  }
}
