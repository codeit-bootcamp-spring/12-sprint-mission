package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("이메일 존재 여부 조회 성공")
  void existsByEmail_success() {
    // given
    persistUserWithStatus("minji", "minji@test.com");

    // when
    boolean result = userRepository.existsByEmail("minji@test.com");

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("이메일 존재 여부 조회 실패 - 존재하지 않는 이메일")
  void existsByEmail_fail_notExists() {
    // given
    persistUserWithStatus("minji", "minji@test.com");

    // when
    boolean result = userRepository.existsByEmail("none@test.com");

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("프로필과 상태를 함께 조회 성공")
  void findAllWithProfileAndStatus_success() {
    // given
    persistUserWithStatus("minji", "minji@test.com");
    persistUserWithStatus("java", "java@test.com");

    entityManager.flush();
    entityManager.clear();

    // when
    List<User> result = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(result).hasSize(2);
    assertThat(result)
        .extracting(User::getUsername)
        .containsExactlyInAnyOrder("minji", "java");
  }

  @Test
  @DisplayName("사용자 이름 기준 정렬 조회 성공")
  void findAll_sortByUsername_success() {
    // given
    persistUserWithStatus("bbb", "bbb@test.com");
    persistUserWithStatus("aaa", "aaa@test.com");

    entityManager.flush();
    entityManager.clear();

    // when
    List<User> result = userRepository.findAll(Sort.by(Sort.Direction.ASC, "username"));

    // then
    assertThat(result)
        .extracting(User::getUsername)
        .containsExactly("aaa", "bbb");
  }

  private User persistUserWithStatus(String username, String email) {
    User user = new User(username, email, "1234", null);
    entityManager.persist(user);

    UserStatus userStatus = new UserStatus(user, Instant.now());
    entityManager.persist(userStatus);

    return user;
  }
}