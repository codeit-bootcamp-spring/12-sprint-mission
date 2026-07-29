package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @InjectMocks
  private BasicUserService userService;


  @Test
  @DisplayName("사용자를 생성할 수 있다")
  void create_success() {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@test.com",
        "testUser",
        "password"
    );

    User savedUser = new User(
        request.email(),
        request.username(),
        request.password(),
        null
    );
    savedUser.initStatus();

    given(userRepository.findByUsername(request.username()))
        .willReturn(Optional.empty());
    given(userRepository.findByEmail(request.email()))
        .willReturn(Optional.empty());
    given(passwordEncoder.encode(request.password()))
        .willReturn("encoded-password");
    given(userRepository.save(any(User.class)))
        .willReturn(savedUser);

    // when
    UserResponse response = userService.create(request, null);

    // then
    assertThat(response.email()).isEqualTo(request.email());
    assertThat(response.username()).isEqualTo(request.username());

    verify(userRepository).save(any(User.class));
    verify(passwordEncoder).encode(request.password());
  }

  @Test
  @DisplayName("중복된 username이면 사용자 생성에 실패한다")
  void create_fail_duplicateUsername() {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "test@test.com",
        "testUser",
        "password"
    );

    User existingUser = new User(
        "other@test.com",
        request.username(),
        "password",
        null
    );

    given(userRepository.findByUsername(request.username()))
        .willReturn(Optional.of(existingUser));

    // when & then
    assertThatThrownBy(() -> userService.create(request, null))
        .isInstanceOf(UserDuplicateException.class);

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 정보를 수정할 수 있다")
  void update_success() {
    // given
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest(
        "new@test.com",
        "newUsername",
        ""
    );

    User user = new User(
        "old@test.com",
        "oldUsername",
        "password",
        null
    );
    user.initStatus();

    given(userRepository.findById(userId))
        .willReturn(Optional.of(user));
    given(userRepository.findByEmail(request.newEmail()))
        .willReturn(Optional.empty());
    given(userRepository.findByUsername(request.newUsername()))
        .willReturn(Optional.empty());
    given(userRepository.save(any(User.class)))
        .willReturn(user);

    // when
    UserResponse response = userService.update(userId, request, null);

    // then
    assertThat(response.email()).isEqualTo(request.newEmail());
    assertThat(response.username()).isEqualTo(request.newUsername());

    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 시 예외가 발생한다")
  void update_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest(
        "new@test.com",
        "newUsername",
        ""
    );

    given(userRepository.findById(userId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.update(userId, request, null))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자를 삭제할 수 있다")
  void delete_success() {
    // given
    UUID userId = UUID.randomUUID();

    User user = new User(
        "test@test.com",
        "testUser",
        "password",
        null
    );

    given(userRepository.findById(userId))
        .willReturn(Optional.of(user));
    given(userStatusRepository.findByUserId(user.getId()))
        .willReturn(Optional.empty());

    // when
    userService.delete(userId);

    // then
    verify(userRepository).deleteById(userId);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 시 예외가 발생한다")
  void delete_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();

    given(userRepository.findById(userId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository, never()).deleteById(userId);
  }

}
