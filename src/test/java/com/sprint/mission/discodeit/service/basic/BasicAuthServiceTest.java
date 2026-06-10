package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.Auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.exception.auth.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicAuthService authService;

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        UUID userId = UUID.randomUUID();
        LoginRequest request = new LoginRequest("user1", "password");

        User user = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        UserStatus userStatus = UserStatus.builder()
                .user(user)
                .lastActiveAt(Instant.now())
                .build();

        UserResponse expectedResponse = new UserResponse(
                userId,
                "user1",
                "user1@test.com",
                null,
                true
        );

        given(userRepository.findByUsername("user1"))
                .willReturn(Optional.of(user));
        given(userStatusRepository.findByUser_Id(userId))
                .willReturn(Optional.of(userStatus));
        given(userMapper.toResponse(user, userStatus))
                .willReturn(expectedResponse);

        UserResponse result = authService.login(request);

        assertThat(result).isEqualTo(expectedResponse);

        then(userRepository).should().findByUsername("user1");
        then(userStatusRepository).should().findByUser_Id(userId);
        then(userMapper).should().toResponse(user, userStatus);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_fail_userNotFound() {
        LoginRequest request = new LoginRequest("unknown", "password");

        given(userRepository.findByUsername("unknown"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findByUsername("unknown");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() {
        LoginRequest request = new LoginRequest("user1", "wrong-password");

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        given(userRepository.findByUsername("user1"))
                .willReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidPasswordException.class);

        then(userRepository).should().findByUsername("user1");
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 상태 없음")
    void login_fail_userStatusNotFound() {
        UUID userId = UUID.randomUUID();
        LoginRequest request = new LoginRequest("user1", "password");

        User user = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        given(userRepository.findByUsername("user1"))
                .willReturn(Optional.of(user));
        given(userStatusRepository.findByUser_Id(userId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UserStatusNotFoundException.class);

        then(userRepository).should().findByUsername("user1");
        then(userStatusRepository).should().findByUser_Id(userId);
    }
}