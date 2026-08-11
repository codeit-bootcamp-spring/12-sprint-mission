package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.user.Role;
import com.sprint.mission.discodeit.entity.user.User;

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
    private TestEntityManager entityManager;

    @Test
    @DisplayName("username으로 사용자 조회 성공")
    void findByUsername_success() {
        // given
        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .role(Role.USER)
                .build();

        userRepository.saveAndFlush(user);
        entityManager.clear();


        // when
        Optional<User> result = userRepository.findByUsername("user1");


        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user1");
        assertThat(result.get().getEmail()).isEqualTo("user1@test.com");
    }

    @Test
    @DisplayName("username으로 사용자 조회 실패 - 존재하지 않음")
    void findByUsername_fail_notFound() {
        // given

        // when
        Optional<User> result = userRepository.findByUsername("unknown");


        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("username 존재 여부 확인 성공")
    void existsByUsername_success() {
        // given
        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .role(Role.USER)
                .build();

        userRepository.saveAndFlush(user);
        entityManager.clear();


        // when
        boolean result = userRepository.existsByUsername("user1");


        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("username 존재 여부 확인 실패")
    void existsByUsername_false() {
        // given

        // when
        boolean result = userRepository.existsByUsername("unknown");


        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("email 존재 여부 확인 성공")
    void existsByEmail_success() {
        // given
        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .role(Role.USER)
                .build();

        userRepository.saveAndFlush(user);
        entityManager.clear();


        // when
        boolean result = userRepository.existsByEmail("user1@test.com");


        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("email 존재 여부 확인 실패")
    void existsByEmail_false() {
        // given

        // when
        boolean result = userRepository.existsByEmail("unknown@test.com");


        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("사용자 단건 조회 성공")
    void findById_success() {
        // given
        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .role(Role.USER)
                .build();


        // when
        User savedUser = userRepository.saveAndFlush(user);
        entityManager.clear();

        Optional<User> result = userRepository.findById(savedUser.getId());


        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedUser.getId());
        assertThat(result.get().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("사용자 단건 조회 실패 - 존재하지 않음")
    void findById_fail_notFound() {
        // given

        // when
        Optional<User> result = userRepository.findById(UUID.randomUUID());


        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자 단건 조회 시 profile 함께 조회")
    void findById_withProfile_success() {
        // given
        BinaryContent profile = BinaryContent.builder()
                .fileName("profile.png")
                .size(100L)
                .contentType("image/png")
                .build();


        // when
        BinaryContent savedProfile = binaryContentRepository.saveAndFlush(profile);

        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .role(Role.USER)
                .profile(savedProfile)
                .build();

        User savedUser = userRepository.saveAndFlush(user);
        entityManager.clear();

        Optional<User> result = userRepository.findById(savedUser.getId());


        // then
        assertThat(result).isPresent();
        assertThat(result.get().getProfile()).isNotNull();
        assertThat(result.get().getProfile().getFileName()).isEqualTo("profile.png");
    }

    @Test
    @DisplayName("ADMIN 권한 사용자 존재 여부 확인")
    void existsByRole_success() {
        User admin = User.builder()
                .username("admin")
                .email("admin@test.com")
                .password("encoded-password")
                .role(Role.ADMIN)
                .build();

        userRepository.saveAndFlush(admin);

        assertThat(userRepository.existsByRole(Role.ADMIN)).isTrue();
    }
}
