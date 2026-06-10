package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("username으로 사용자를 조회할 수 있다")
  void findByUsername_success() {
    // given
    User user = createUser(
        "test@test.com",
        "testUser"
    );

    userRepository.save(user);

    // when
    Optional<User> result = userRepository.findByUsername("testUser");

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getUsername()).isEqualTo("testUser");
    assertThat(result.get().getEmail()).isEqualTo("test@test.com");
  }

  @Test
  @DisplayName("존재하지 않는 username으로 조회하면 empty를 반환한다")
  void findByUsername_fail_notFound() {
    // when
    Optional<User> result = userRepository.findByUsername("unknownUser");

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("email로 사용자를 조회할 수 있다")
  void findByEmail_success() {
    // given
    User user = createUser(
        "test@test.com",
        "testUser"
    );

    userRepository.save(user);

    // when
    Optional<User> result = userRepository.findByEmail("test@test.com");

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("test@test.com");
    assertThat(result.get().getUsername()).isEqualTo("testUser");
  }

  @Test
  @DisplayName("존재하지 않는 email로 조회하면 empty를 반환한다")
  void findByEmail_fail_notFound() {
    // when
    Optional<User> result = userRepository.findByEmail("unknown@test.com");

    // then
    assertThat(result).isEmpty();
  }

  private User createUser(String email, String username) {
    return new User(
        email,
        username,
        "password",
        null
    );
  }

}