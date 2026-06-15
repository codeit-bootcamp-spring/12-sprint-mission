package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.any;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks
  private BasicUserService userService;

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;

  // create

  @Test
  @DisplayName("create 성공 - 프로필 없이 유저 생성")
  void create_success() {
    // given
    UserCreateRequest request = new UserCreateRequest("홍길동", "hong@example.com", "password123");
    User mockUser = new User("홍길동", "hong@example.com", "password123", null);
    UserResponse mockResponse = new UserResponse(UUID.randomUUID(), "홍길동", "hong@example.com", null,
        false);

    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(false);
    given(userRepository.save(any(User.class))).willReturn(mockUser);
    given(userMapper.toResponse(any(User.class))).willReturn(mockResponse);

    // when
    UserResponse result = userService.create(request, Optional.empty());

    // then
    assertThat(result.username()).isEqualTo("홍길동");
    assertThat(result.email()).isEqualTo("hong@example.com");
    then(userRepository).should().save(any(User.class));
    then(userStatusRepository).should().save(any(UserStatus.class));
  }

  @Test
  @DisplayName("create 실패 - 이메일 중복")
  void create_fail_duplicateEmail() {
    // given
    UserCreateRequest request = new UserCreateRequest("홍길동", "hong@example.com", "password123");
    given(userRepository.existsByEmail(request.email())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
    then(userRepository).should().existsByEmail(request.email());
    then(userRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  @DisplayName("create 실패 - 유저명 중복")
  void create_fail_duplicateUsername() {
    // given
    UserCreateRequest request = new UserCreateRequest("홍길동", "hong@example.com", "password123");
    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  // update

  @Test
  @DisplayName("update 성공")
  void update_success() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("새이름", "new@example.com", "newPassword");
    User mockUser = new User("홍길동", "hong@example.com", "password123", null);
    UserResponse mockResponse = new UserResponse(userId, "새이름", "new@example.com", null, false);

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
    given(userRepository.existsByUsername(request.newUsername())).willReturn(false);
    given(userMapper.toResponse(any(User.class))).willReturn(mockResponse);

    // when
    UserResponse result = userService.update(userId, request, Optional.empty());

    // then
    assertThat(result.username()).isEqualTo("새이름");
    assertThat(result.email()).isEqualTo("new@example.com");
  }

  @Test
  @DisplayName("update 실패 - 존재하지 않는 유저")
  void update_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("새이름", "new@example.com", "newPassword");
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  // delete

  @Test
  @DisplayName("delete 성공")
  void delete_success() {
    // given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(true);

    // when
    userService.delete(userId);

    // then
    then(userRepository).should().deleteById(userId);
  }

  @Test
  @DisplayName("delete 실패 - 존재하지 않는 유저")
  void delete_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
    then(userRepository).should().existsById(userId);
    then(userRepository).shouldHaveNoMoreInteractions();
  }
}