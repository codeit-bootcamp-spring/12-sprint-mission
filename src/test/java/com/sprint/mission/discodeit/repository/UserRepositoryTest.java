package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.TestJpaAuditingConfig;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

// @Import(TestJpaAuditingConfig.class): @DataJpaTest 슬라이스에서 @EnableJpaAuditing 활성화
// 각 테스트 클래스에 @EnableJpaAuditing을 직접 붙이면 빈 중복 문제 발생
@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
class UserRepositoryTest {

  @Autowired UserRepository userRepository;

  private User savedUser;

  @BeforeEach
  void setUp() {
    savedUser = userRepository.save(new User("testuser", "test@email.com", "password", null));
  }

  @Test
  @DisplayName("이메일로 존재 여부 확인 - 존재하는 경우")
  void existsByEmail_exists() {
    assertThat(userRepository.existsByEmail("test@email.com")).isTrue();
  }

  @Test
  @DisplayName("이메일로 존재 여부 확인 - 존재하지 않는 경우")
  void existsByEmail_notExists() {
    assertThat(userRepository.existsByEmail("notexist@email.com")).isFalse();
  }

  @Test
  @DisplayName("사용자명으로 존재 여부 확인 - 존재하는 경우")
  void existsByUsername_exists() {
    assertThat(userRepository.existsByUsername("testuser")).isTrue();
  }

  @Test
  @DisplayName("사용자명으로 존재 여부 확인 - 존재하지 않는 경우")
  void existsByUsername_notExists() {
    assertThat(userRepository.existsByUsername("nobody")).isFalse();
  }

  @Test
  @DisplayName("사용자명으로 User 조회 성공")
  void findByUsername_success() {
    Optional<User> found = userRepository.findByUsername("testuser");
    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("test@email.com");
  }

  @Test
  @DisplayName("존재하지 않는 사용자명으로 조회 시 빈 결과")
  void findByUsername_notFound() {
    Optional<User> found = userRepository.findByUsername("nobody");
    assertThat(found).isEmpty();
  }
}
