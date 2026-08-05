package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UsernameAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private UserMapper userMapper;

  @Mock
  private BinaryContentMapper binaryContentMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private SessionManager sessionManager;

  @InjectMocks
  private BasicUserService userService;

  private UUID userId;

  private UserCreateRequest userCreateRequest;
  private BinaryContentCreateRequest binaryContentCreateRequest;
  private BinaryContent profile;
  private BinaryContentDto profileDto;
  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();

    userCreateRequest = new UserCreateRequest(
        "test1",
        "test1@test.com",
        "1234"
    );

    binaryContentCreateRequest = new BinaryContentCreateRequest(
        "profile.png",
        "image/png",
        "profile".getBytes(StandardCharsets.UTF_8)
    );

    profile = BinaryContent.builder()
        .id(UUID.randomUUID())
        .fileName(binaryContentCreateRequest.fileName())
        .contentType(binaryContentCreateRequest.contentType())
        .size((long) binaryContentCreateRequest.bytes().length)
        .build();

    profileDto = new BinaryContentDto(
        profile.getId(),
        profile.getFileName(),
        profile.getSize(),
        profile.getContentType()
    );

    user = User.builder()
        .id(userId)
        .username(userCreateRequest.username())
        .email(userCreateRequest.email())
        .password(userCreateRequest.password())
        .profile(null)
        .build();

    userDto = new UserDto(
        userId,
        userCreateRequest.username(),
        userCreateRequest.email(),
        null,
        false
    );
  }

  @Test
  @DisplayName("user_create_success_with_profile")
  void user_create_success() {
    user.setProfile(profile);
    userDto = new UserDto(
        userId,
        userCreateRequest.username(),
        userCreateRequest.email(),
        profileDto,
        false
    );

    given(userRepository.existsByUsername(any())).willReturn(false);
    given(userRepository.existsByEmail(any())).willReturn(false);
    given(passwordEncoder.encode(userCreateRequest.password())).willReturn("encoded-password");
    given(binaryContentMapper.toEntity(any())).willReturn(profile);
    given(binaryContentRepository.save(any())).willReturn(profile);
    given(userMapper.toEntity(any(UserCreateRequest.class), eq(profile))).willReturn(user);
    given(userRepository.save(any())).willReturn(user);
    given(userMapper.toDto(any())).willReturn(userDto);

    UserDto result = userService.create(userCreateRequest, Optional.of(binaryContentCreateRequest));

    assertNotNull(result);
    assertEquals(userDto, result);

    verify(userRepository, times(1)).save(user);
    verify(binaryContentStorage, times(1))
        .put(
            eq(profile.getId()),
            aryEq(binaryContentCreateRequest.bytes()),
            eq(binaryContentCreateRequest.contentType())
        );
  }

  @Test
  @DisplayName("user_create_failed")
  void user_create_failed() {
    given(userRepository.existsByUsername(any())).willReturn(true);

    assertThatThrownBy(() -> userService.create(userCreateRequest, Optional.empty()))
        .isInstanceOf(UsernameAlreadyExistsException.class);

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("user_findDetailById_success")
  void user_findDetailById_success() {
    given(userRepository.findDetailById(userId)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userDto);

    UserDto result = userService.findDetailById(userId);

    assertEquals(userDto, result);
  }

  @Test
  @DisplayName("user_update_success")
  void user_update_success() {
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "test",
        "update@test.com",
        "12345"
    );

    given(userRepository.findDetailById(any())).willReturn(Optional.of(user));
    given(userRepository.existsByEmail(any())).willReturn(false);
    given(passwordEncoder.encode(userUpdateRequest.newPassword())).willReturn("encoded-password");
    given(userRepository.save(any())).willReturn(user);
    given(userMapper.toDto(any())).willReturn(userDto);

    UserDto result = userService.update(userId, userUpdateRequest, Optional.empty());

    assertThat(result).isEqualTo(userDto);
    assertEquals(userUpdateRequest.newUsername(), user.getUsername());
    assertEquals(userUpdateRequest.newEmail(), user.getEmail());
    assertEquals("encoded-password", user.getPassword());
  }

  @Test
  @DisplayName("user_update_failed")
  void user_update_failed() {
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "update",
        "update@test.com",
        "12345"
    );

    given(userRepository.findDetailById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(userId, userUpdateRequest, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("user_delete_success")
  void user_delete_success() {
    given(userRepository.findDetailById(userId)).willReturn(Optional.of(user));

    userService.delete(userId);

    verify(userRepository).delete(user);
  }

  @Test
  @DisplayName("user_delete_failed")
  void user_delete_failed() {
    given(userRepository.findDetailById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository, never()).delete(any());
  }
}
