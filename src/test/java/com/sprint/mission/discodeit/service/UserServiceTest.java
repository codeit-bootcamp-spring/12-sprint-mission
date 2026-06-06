package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
    given(userMapper.toDto(any(User.class))).willReturn(response);

    // when
    UserDto result = userService.create(request, Optional.empty());

    // then
    assertThat(result.username()).isEqualTo("minji");
    assertThat(result.email()).isEqualTo("minji@test.com");

    then(userRepository).should().existsByEmail("minji@test.com");
    then(userRepository).should().existsByUsername("minji");
    then(userRepository).should().save(any(User.class));
    then(userMapper).should().toDto(any(User.class));
  }

  @Test
  @DisplayName("사용자 생성 성공 - 프로필 이미지 있음")
  void create_success_withProfile() {
    // given
    byte[] bytes = "profile".getBytes();

    UserCreateRequest request = new UserCreateRequest(
        "minji",
        "minji@test.com",
        "1234"
    );

    BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(
        "profile.png",
        "image/png",
        bytes
    );

    UUID profileId = UUID.randomUUID();

    doAnswer(invocation -> {
      BinaryContent binaryContent = invocation.getArgument(0);
      ReflectionTestUtils.setField(binaryContent, "id", profileId);
      return binaryContent;
    }).when(binaryContentRepository).save(any(BinaryContent.class));

    UserDto response = new UserDto(
        UUID.randomUUID(),
        "minji",
        "minji@test.com",
        null,
        false
    );

    given(userRepository.existsByEmail("minji@test.com")).willReturn(false);
    given(userRepository.existsByUsername("minji")).willReturn(false);
    given(userMapper.toDto(any(User.class))).willReturn(response);

    // when
    UserDto result = userService.create(request, Optional.of(profileRequest));

    // then
    assertThat(result.username()).isEqualTo("minji");
    assertThat(result.email()).isEqualTo("minji@test.com");

    then(binaryContentRepository).should().save(any(BinaryContent.class));
    then(binaryContentStorage).should().put(profileId, bytes);
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
    then(userRepository).should(never()).existsByUsername("minji");
    then(userRepository).should(never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 사용자 이름 중복")
  void create_fail_duplicateUsername() {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "minji",
        "minji@test.com",
        "1234"
    );

    given(userRepository.existsByEmail("minji@test.com")).willReturn(false);
    given(userRepository.existsByUsername("minji")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(DuplicateUsernameException.class);

    then(userRepository).should().existsByEmail("minji@test.com");
    then(userRepository).should().existsByUsername("minji");
    then(userRepository).should(never()).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 단건 조회 성공")
  void find_success() {
    // given
    UUID userId = UUID.randomUUID();

    User user = new User("minji", "minji@test.com", "1234", null);

    UserDto response = new UserDto(
        user.getId(),
        "minji",
        "minji@test.com",
        null,
        false
    );

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(response);

    // when
    UserDto result = userService.find(userId);

    // then
    assertThat(result.username()).isEqualTo("minji");
    assertThat(result.email()).isEqualTo("minji@test.com");

    then(userRepository).should().findById(userId);
    then(userMapper).should().toDto(user);
  }

  @Test
  @DisplayName("사용자 단건 조회 실패 - 사용자 없음")
  void find_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.find(userId))
        .isInstanceOf(UserNotFoundException.class);

    then(userRepository).should().findById(userId);
    then(userMapper).should(never()).toDto(any(User.class));
  }

  @Test
  @DisplayName("사용자 목록 조회 성공")
  void findAll_success() {
    // given
    User user1 = new User("minji", "minji@test.com", "1234", null);
    User user2 = new User("jisu", "jisu@test.com", "1234", null);

    UserDto response1 = new UserDto(
        user1.getId(),
        "minji",
        "minji@test.com",
        null,
        false
    );

    UserDto response2 = new UserDto(
        user2.getId(),
        "jisu",
        "jisu@test.com",
        null,
        false
    );

    given(userRepository.findAllWithProfileAndStatus()).willReturn(List.of(user1, user2));
    given(userMapper.toDto(user1)).willReturn(response1);
    given(userMapper.toDto(user2)).willReturn(response2);

    // when
    List<UserDto> result = userService.findAll();

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).username()).isEqualTo("minji");
    assertThat(result.get(1).username()).isEqualTo("jisu");

    then(userRepository).should().findAllWithProfileAndStatus();
    then(userMapper).should().toDto(user1);
    then(userMapper).should().toDto(user2);
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
    then(userRepository).should().existsByEmail("new@test.com");
    then(userRepository).should().existsByUsername("newName");
    then(userMapper).should().toDto(user);
  }

  @Test
  @DisplayName("사용자 수정 성공 - 프로필 이미지 있음")
  void update_success_withProfile() {
    // given
    UUID userId = UUID.randomUUID();
    byte[] bytes = "profile".getBytes();

    User user = new User("oldName", "old@test.com", "oldPassword", null);

    UserUpdateRequest request = new UserUpdateRequest(
        "newName",
        "new@test.com",
        "newPassword"
    );

    BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(
        "profile.png",
        "image/png",
        bytes
    );

    UUID profileId = UUID.randomUUID();

    doAnswer(invocation -> {
      BinaryContent binaryContent = invocation.getArgument(0);
      ReflectionTestUtils.setField(binaryContent, "id", profileId);
      return binaryContent;
    }).when(binaryContentRepository).save(any(BinaryContent.class));

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
    UserDto result = userService.update(userId, request, Optional.of(profileRequest));

    // then
    assertThat(result.username()).isEqualTo("newName");
    assertThat(result.email()).isEqualTo("new@test.com");

    then(binaryContentRepository).should().save(any(BinaryContent.class));
    then(binaryContentStorage).should().put(profileId, bytes);
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
  @DisplayName("사용자 수정 실패 - 이메일 중복")
  void update_fail_duplicateEmail() {
    // given
    UUID userId = UUID.randomUUID();

    User user = new User("oldName", "old@test.com", "oldPassword", null);

    UserUpdateRequest request = new UserUpdateRequest(
        "newName",
        "new@test.com",
        "newPassword"
    );

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail("new@test.com")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(DuplicateEmailException.class);

    then(userRepository).should().findById(userId);
    then(userRepository).should().existsByEmail("new@test.com");
    then(userRepository).should(never()).existsByUsername("newName");
    then(userMapper).should(never()).toDto(any(User.class));
  }

  @Test
  @DisplayName("사용자 수정 실패 - 사용자 이름 중복")
  void update_fail_duplicateUsername() {
    // given
    UUID userId = UUID.randomUUID();

    User user = new User("oldName", "old@test.com", "oldPassword", null);

    UserUpdateRequest request = new UserUpdateRequest(
        "newName",
        "new@test.com",
        "newPassword"
    );

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail("new@test.com")).willReturn(false);
    given(userRepository.existsByUsername("newName")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(DuplicateUsernameException.class);

    then(userRepository).should().findById(userId);
    then(userRepository).should().existsByEmail("new@test.com");
    then(userRepository).should().existsByUsername("newName");
    then(userMapper).should(never()).toDto(any(User.class));
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