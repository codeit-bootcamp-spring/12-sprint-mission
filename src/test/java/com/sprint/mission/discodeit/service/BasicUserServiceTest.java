package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.any;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock UserRepository userRepository;
  @Mock BinaryContentRepository binaryContentRepository;
  @Mock BinaryContentStorage binaryContentStorage;
  @Mock UserMapper userMapper;
  @Mock org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  @InjectMocks BasicUserService userService;

  private UUID userId;
  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = new User("testuser", "test@email.com", "password123!", null);
    userDto = new UserDto(userId, "testuser", "test@email.com", null, true, Role.USER);
  }

  // ── create ──────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 생성 성공")
  void create_success() {
    // given
    UserCreateRequest req = new UserCreateRequest("testuser", "test@email.com", "password123!");

    given(userRepository.existsByEmail(req.email())).willReturn(false);
    given(userRepository.existsByUsername(req.username())).willReturn(false);
    given(passwordEncoder.encode(req.password())).willReturn("encoded-password");
    given(userRepository.save(any(User.class))).willReturn(user);
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // when
    UserDto result = userService.create(req, Optional.empty());

    // then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("testuser");
    then(userRepository).should().save(any(User.class));
  }

  @Test
  @DisplayName("이메일 중복 시 UserAlreadyExistsException 발생")
  void create_duplicateEmail_throwsException() {
    // given
    UserCreateRequest req = new UserCreateRequest("testuser", "dup@email.com", "password123!");
    given(userRepository.existsByEmail(req.email())).willReturn(true);

    // when / then
    assertThatThrownBy(() -> userService.create(req, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("사용자명 중복 시 UserAlreadyExistsException 발생")
  void create_duplicateUsername_throwsException() {
    // given
    UserCreateRequest req = new UserCreateRequest("dupuser", "new@email.com", "password123!");
    given(userRepository.existsByEmail(req.email())).willReturn(false);
    given(userRepository.existsByUsername(req.username())).willReturn(true);

    // when / then
    assertThatThrownBy(() -> userService.create(req, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  // ── update ──────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 수정 성공")
  void update_success() {
    // given
    UserUpdateRequest req = new UserUpdateRequest("newuser", "new@email.com", "newpass123!");
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail(req.newEmail())).willReturn(false);
    given(userRepository.existsByUsername(req.newUsername())).willReturn(false);
    given(passwordEncoder.encode(req.newPassword())).willReturn("encoded-password");
    given(userRepository.save(any(User.class))).willReturn(user);
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // when
    UserDto result = userService.update(userId, req, Optional.empty());

    // then
    assertThat(result).isNotNull();
    then(userRepository).should().save(any(User.class));
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 시 UserNotFoundException 발생")
  void update_notFound_throwsException() {
    // given
    UserUpdateRequest req = new UserUpdateRequest("newuser", "new@email.com", "newpass123!");
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> userService.update(userId, req, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  // ── delete ──────────────────────────────────────────────────

  @Test
  @DisplayName("사용자 삭제 성공")
  void delete_success() {
    // given
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // when
    userService.delete(userId);

    // then
    then(userRepository).should().delete(user);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 시 UserNotFoundException 발생")
  void delete_notFound_throwsException() {
    // given
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when / then
    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
