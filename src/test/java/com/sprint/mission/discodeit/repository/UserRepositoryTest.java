package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager em;

  private User createUser(String username, String email) {
    BinaryContent profile = new BinaryContent(username + ".jpg", 1024L, "image/png");
    User user = new User(username, email, "password1234!", profile, Role.USER);
    return userRepository.save(user);
  }

  // ── findByUsername ──────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 이름 조회 테스트(성공)")
  public void findByUsername() {
    String username = "testUser";
    User user = createUser(username, "test@test.com");
    em.flush();
    em.clear();

    Optional<User> result = userRepository.findByUsername(username);
    assertThat(result).isPresent();
    assertThat(result.get().getUsername()).isEqualTo(username);
  }

  @Test
  @DisplayName("사용자 이름 조회 테스트(실패)")
  public void findByUsername_notFound() {
    String username = "testUser10";
    Optional<User> result = userRepository.findByUsername(username);
    assertThat(result).isNotPresent();
  }

  // ── existsByEmail ───────────────────────────────────────────────────────

  @Test
  @DisplayName("이메일 존재 여부 테스트(성공)")
  public void existsByEmail() {
    String username = "testUser";
    String email = "test10@test.com";
    User user = createUser(username, email);
    em.flush();
    em.clear();

    boolean result = userRepository.existsByEmail(email);
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("이메일 존재 여부 테스트(실패)")
  public void existsByEmail_notExists() {
    String email = "test10@test.com";
    boolean result = userRepository.existsByEmail(email);
    assertThat(result).isFalse();
  }

  // ── existsByUsername ────────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 이름 존재 여부 테스트(성공)")
  public void existsByUsername() {
    String username = "testUser";
    String email = "test10@test.com";
    User user = createUser(username, email);
    em.flush();
    em.clear();

    boolean result = userRepository.existsByUsername(username);
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("사용자 이름 존재 여부 테스트(실패)")
  public void existsByUsername_notExists() {
    String username = "testUser10";
    boolean result = userRepository.existsByUsername(username);
    assertThat(result).isFalse();
  }

  // ── findAllWithProfileAndUserStatus ─────────────────────────────────────

  @Test
  @DisplayName("프로필과 사용자 상태를 함께 조회하는 테스트(성공)")
  public void findAllWithProfileAndUserStatus() {
    User user1 = createUser("testUser1", "test1@test.com");
    User user2 = createUser("testUser2", "test2@test.com");
    em.flush();
    em.clear();

    List<User> users = userRepository.findAllWithProfile();
    assertThat(users).hasSize(2);
    assertThat(users.get(0).getProfile()).isNotNull();
    assertThat(users).extracting(User::getUsername)
        .containsExactlyInAnyOrder("testUser1", "testUser2");
  }
}
