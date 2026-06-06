package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
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

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("사용자 생성 성공")
  void create_success() {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "minji",
        "minji@test.com",
        "1234"
    );

    User user = new User("minji", "minji@test.com", "1234", null);

    UserDto response = new UserDto(
        user.getId(),
        "minji",
        "minji@test.com",
        null,
        false
    );

    given(userRepository.existsByEmail("minji@test.com")).willReturn(false);
    given(userRepository.existsByUsername("minji")).willReturn(false);
    given(userRepository.save(any(User.class))).willReturn(user);
    given(userMapper.toDto(any(User.class))).willReturn(response);

    // when
    UserDto result = userService.create(request, Optional.empty());

    // then
    assertThat(result.username()).isEqualTo("minji");
    assertThat(result.email()).isEqualTo("minji@test.com");

    then(userRepository).should().save(any(User.class));
    then(userMapper).should().toDto(any(User.class));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 이메일 중복")
  void create_fail_duplicateEmail() {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "minji",
        "minji@test.com",
        "1234"
    );

    given(userRepository.existsByEmail("minji@test.com")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(DuplicateEmailException.class);

    then(userRepository).should().existsByEmail("minji@test.com");
    then(userRepository).should(never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void update_success() {
    // given
    UUID userId = UUID.randomUUID();

    User user = new User("oldName", "old@test.com", "oldPassword", null);

    UserUpdateRequest request = new UserUpdateRequest(
        "newName",
        "new@test.com",
        "newPassword"
    );

    UserDto response = new UserDto(
        user.getId(),
        "newName",
        "new@test.com",
        null,
        false
    );

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail("new@test.com")).willReturn(false);
    given(userRepository.existsByUsername("newName")).willReturn(false);
    given(userMapper.toDto(user)).willReturn(response);

    // when
    UserDto result = userService.update(userId, request, Optional.empty());

    // then
    assertThat(result.username()).isEqualTo("newName");
    assertThat(result.email()).isEqualTo("new@test.com");

    then(userRepository).should().findById(userId);
    then(userMapper).should().toDto(user);
  }

  @Test
  @DisplayName("사용자 수정 실패 - 사용자 없음")
  void update_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest(
        "newName",
        "new@test.com",
        "newPassword"
    );

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);

    then(userRepository).should().findById(userId);
    then(userRepository).should(never()).existsByEmail("new@test.com");
  }

  @Test
  @DisplayName("사용자 삭제 성공")
  void delete_success() {
    // given
    UUID userId = UUID.randomUUID();

    given(userRepository.existsById(userId)).willReturn(true);

    // when
    userService.delete(userId);

    // then
    then(userRepository).should().existsById(userId);
    then(userRepository).should().deleteById(userId);
  }

  @Test
  @DisplayName("사용자 삭제 실패 - 사용자 없음")
  void delete_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();

    given(userRepository.existsById(userId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);

    then(userRepository).should().existsById(userId);
    then(userRepository).should(never()).deleteById(userId);
  }
}