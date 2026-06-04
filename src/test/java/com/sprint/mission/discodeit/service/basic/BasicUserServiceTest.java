package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;

import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
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
public class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicUserService userService;

  private UUID id;
  private String username;
  private String email;
  private String password;
  private User user;
  private UserDto userDto;
  Instant now;

  @BeforeEach
  public void setUp() {
    id = UuidCreator.getTimeOrderedEpoch();
    username = "testUser";
    password = "qwer1234!";
    email = "test@test.com";
    now = Instant.now();
    user = new User(username, email, password, null);
    ReflectionTestUtils.setField(user, "id", id);
    userDto = new UserDto(id, username, email, null, true);
  }

  @Test
  @DisplayName("사용자 생성 테스트(성공)")
  public void create_success() {
    UserCreateRequest request = new UserCreateRequest(username, email, password);
    given(userRepository.existsByEmail(email)).willReturn(false);
    given(userRepository.existsByUsername(username)).willReturn(false);
    given(userRepository.save(any())).willReturn(user);
    given(userMapper.toDto(user)).willReturn(userDto);

    UserDto result = userService.create(request, Optional.empty());

    assertThat(result).isEqualTo(userDto);
  }

  @Test
  @DisplayName("사용자 생성 테스트(실패 - 이메일 중복)")
  public void create_fail_email_duplicate() {
    UserCreateRequest request = new UserCreateRequest(username, email, password);
    given(userRepository.existsByEmail(email)).willReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistException.class);
  }

  @Test
  @DisplayName("사용자 생성 테스트(실패 - 사용자 이름 중복)")
  public void create_fail_username_duplicate() {
    UserCreateRequest request = new UserCreateRequest(username, email, password);
    given(userRepository.existsByEmail(email)).willReturn(false);
    given(userRepository.existsByUsername(username)).willReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistException.class);
  }

  @Test
  @DisplayName("사용자 조회 테스트(성공)")
  public void find_success() {
    given(userRepository.findById(id)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userDto);

    UserDto result = userService.find(id);

    assertThat(result).isEqualTo(userDto);
  }

  @Test
  @DisplayName("사용자 조회 테스트(실패 - 사용자 없음)")
  public void find_fail_not_found() {
    given(userRepository.findById(id)).willReturn(Optional.empty());
    assertThatThrownBy(() -> userService.find(id))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 수정 테스트(성공)")
  public void update_success() {
    String newUsername = "updatedUser";
    String newEmail = "updated@test.com";
    String newPassword = "newPass1234!";

    UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);
    UserDto updatedUserDto = new UserDto(id, newUsername, newEmail, null, true);

    given(userRepository.findById(id)).willReturn(Optional.of(user));

    given(userRepository.existsByEmail(newEmail)).willReturn(false);
    given(userRepository.existsByUsername(newUsername)).willReturn(false);

    given(userRepository.save(any())).willReturn(user);
    given(userMapper.toDto(user)).willReturn(updatedUserDto);

    UserDto result = userService.update(id, request, Optional.empty());

    assertThat(result).isEqualTo(updatedUserDto);
    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("사용자 수정 테스트(실패 - 사용자 없음)")
  public void update_fail_not_found() {
    String newUsername = "updatedUser";
    String newEmail = "updated@test.com";
    String newPassword = "newPass1234!";

    UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);
    given(userRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(id, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("사용자 삭제 테스트(성공)")
  public void delete_success() {
    given(userRepository.findById(id)).willReturn(Optional.of(user));

    userService.delete(id);
    verify(userRepository).deleteById(id);
  }

  @Test
  @DisplayName("사용자 삭제 테스트(실패 - 사용자 없음)")
  public void delete_fail_not_found() {
    given(userRepository.findById(id)).willReturn(Optional.empty());
    assertThatThrownBy(() -> userService.delete(id))
        .isInstanceOf(UserNotFoundException.class);
  }
}
