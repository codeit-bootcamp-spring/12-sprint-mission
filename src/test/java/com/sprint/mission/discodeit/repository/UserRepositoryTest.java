package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("username으로 사용자 조회 성공")
    void findByUsername_success() {
        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        userRepository.saveAndFlush(user);
        entityManager.clear();

        Optional<User> result = userRepository.findByUsername("user1");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user1");
        assertThat(result.get().getEmail()).isEqualTo("user1@test.com");
    }

    @Test
    @DisplayName("username으로 사용자 조회 실패 - 존재하지 않음")
    void findByUsername_fail_notFound() {
        Optional<User> result = userRepository.findByUsername("unknown");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("username 존재 여부 확인 성공")
    void existsByUsername_success() {
        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        userRepository.saveAndFlush(user);
        entityManager.clear();

        boolean result = userRepository.existsByUsername("user1");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("username 존재 여부 확인 실패")
    void existsByUsername_false() {
        boolean result = userRepository.existsByUsername("unknown");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("email 존재 여부 확인 성공")
    void existsByEmail_success() {
        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        userRepository.saveAndFlush(user);
        entityManager.clear();

        boolean result = userRepository.existsByEmail("user1@test.com");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("email 존재 여부 확인 실패")
    void existsByEmail_false() {
        boolean result = userRepository.existsByEmail("unknown@test.com");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("사용자 단건 조회 성공")
    void findById_success() {
        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        User savedUser = userRepository.saveAndFlush(user);
        entityManager.clear();

        Optional<User> result = userRepository.findById(savedUser.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedUser.getId());
        assertThat(result.get().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("사용자 단건 조회 실패 - 존재하지 않음")
    void findById_fail_notFound() {
        Optional<User> result = userRepository.findById(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자 단건 조회 시 profile과 status 함께 조회")
    void findById_withProfileAndStatus_success() {
        BinaryContent profile = BinaryContent.builder()
                .fileName("profile.png")
                .size(100L)
                .contentType("image/png")
                .build();

        BinaryContent savedProfile = binaryContentRepository.saveAndFlush(profile);

        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .profile(savedProfile)
                .build();

        User savedUser = userRepository.saveAndFlush(user);

        UserStatus status = UserStatus.builder()
                .user(savedUser)
                .lastActiveAt(Instant.now())
                .build();

        userStatusRepository.saveAndFlush(status);
        entityManager.clear();

        Optional<User> result = userRepository.findById(savedUser.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getProfile()).isNotNull();
        assertThat(result.get().getProfile().getFileName()).isEqualTo("profile.png");
        assertThat(result.get().getStatus()).isNotNull();
        assertThat(result.get().getStatus().getUser().getId()).isEqualTo(savedUser.getId());
    }
}