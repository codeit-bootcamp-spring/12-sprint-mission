package com.sprint.mission.discodeit.service.basic;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class BasicAuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicAuthService authService;

  private UUID userId;
  private String username;
  private String password;
  private String email;
  private User user;
  private UserDto userDto;

  @BeforeEach
  public void setUp() {
    userId = UuidCreator.getTimeOrderedEpoch();
    username = "testUser";
    password = "password1234!";
    email = "test@test.com";
    user = new User(username, email, password, null);
    ReflectionTestUtils.setField(user, "id", userId);
    userDto = new UserDto(userId, username, email, null, true);
  }

  // ── login ──────────────────────────────────────────────────────────────

  @Test
  @DisplayName("로그인 테스트(성공)")
  public void login() {
    LoginRequest loginRequest = new LoginRequest(username, password);

    given(userRepository.findByUsername(any(String.class)))
        .willReturn(Optional.of(user));
    given(userMapper.toDto(any(User.class)))
        .willReturn(userDto);

    UserDto result = authService.login(loginRequest);

    assertThat(result).isEqualTo(userDto);
    verify(userRepository).findByUsername(username);
    verify(userMapper).toDto(user);
  }

  @Test
  @DisplayName("로그인 테스트(실패 - 사용자 없음)")
  public void login_userNotFound() {
    LoginRequest loginRequest = new LoginRequest(username, password);
    given(userRepository.findByUsername(any(String.class))).willReturn(Optional.empty());
    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(UserNotFoundException.class);
    verify(userRepository).findByUsername(username);
  }

  @Test
  @DisplayName("로그인 테스트(실패 - 비밀번호 불일치)")
  public void login_passwordMismatch() {
    LoginRequest loginRequest = new LoginRequest(username, "wrongPassword");
    given(userRepository.findByUsername(any(String.class))).willReturn(Optional.of(user));
    assertThatThrownBy(() -> authService.login(loginRequest))
        .isInstanceOf(InvalidCredentialsException.class);
  }
}
