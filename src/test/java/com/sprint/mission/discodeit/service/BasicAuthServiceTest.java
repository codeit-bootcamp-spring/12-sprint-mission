package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.InvalidCredentialsException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
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

  @Mock UserRepository userRepository;
  @Mock UserService userService;

  @InjectMocks BasicAuthService authService;

  @Test
  @DisplayName("로그인 성공")
  void login_success() {
    UUID userId = UUID.randomUUID();
    User user = new User("testuser", "test@email.com", "password123!", null);
    UserDto userDto = new UserDto(userId, "testuser", "test@email.com", null, true);

    given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
    given(userService.find(user.getId())).willReturn(userDto);

    UserDto result = authService.login(new LoginRequest("testuser", "password123!"));

    assertThat(result.username()).isEqualTo("testuser");
  }

  @Test
  @DisplayName("존재하지 않는 사용자명으로 로그인 시 예외 발생")
  void login_userNotFound_throwsException() {
    given(userRepository.findByUsername("nobody")).willReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(new LoginRequest("nobody", "password123!")))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  @DisplayName("비밀번호 불일치 시 예외 발생")
  void login_wrongPassword_throwsException() {
    User user = new User("testuser", "test@email.com", "password123!", null);
    given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

    assertThatThrownBy(() -> authService.login(new LoginRequest("testuser", "wrongpassword")))
        .isInstanceOf(InvalidCredentialsException.class);
  }
}
