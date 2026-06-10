package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.service.BinaryContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserStatusMapper userStatusMapper;

    @Mock
    private BinaryContentService binaryContentService;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("사용자 생성 성공 - 프로필 없음")
    void create_success_withoutProfile() {
        UserCreateRequest request = new UserCreateRequest(
                "user1",
                "user1@test.com",
                "password"
        );

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        User savedUser = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        UserStatus userStatus = UserStatus.builder()
                .user(savedUser)
                .lastActiveAt(Instant.now())
                .build();

        UserResponse expectedResponse = new UserResponse(
                userId,
                "user1",
                "user1@test.com",
                null,
                true
        );

        given(userRepository.existsByUsername("user1")).willReturn(false);
        given(userRepository.existsByEmail("user1@test.com")).willReturn(false);
        given(userMapper.toEntity(request, null)).willReturn(user);
        given(userRepository.save(user)).willReturn(savedUser);
        given(userStatusMapper.toEntity(any(User.class), any(Instant.class))).willReturn(userStatus);
        given(userStatusRepository.save(userStatus)).willReturn(userStatus);
        given(userMapper.toResponse(savedUser, userStatus)).willReturn(expectedResponse);

        UserResponse result = userService.create(request, Optional.empty());

        assertThat(result).isEqualTo(expectedResponse);

        then(userRepository).should().existsByUsername("user1");
        then(userRepository).should().existsByEmail("user1@test.com");
        then(userMapper).should().toEntity(request, null);
        then(userRepository).should().save(user);
        then(userStatusRepository).should().save(userStatus);
        then(userMapper).should().toResponse(savedUser, userStatus);
        then(binaryContentService).should(never()).createBinaryContent(any());
    }

    @Test
    @DisplayName("사용자 생성 성공 - 프로필 있음")
    void create_success_withProfile() {
        UserCreateRequest request = new UserCreateRequest(
                "user1",
                "user1@test.com",
                "password"
        );

        BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(
                "image".getBytes(),
                "profile.png",
                "image/png"
        );

        BinaryContent profile = BinaryContent.builder()
                .id(UUID.randomUUID())
                .fileName("profile.png")
                .size(5L)
                .contentType("image/png")
                .build();

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .profile(profile)
                .build();

        User savedUser = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .profile(profile)
                .build();

        UserStatus userStatus = UserStatus.builder()
                .user(savedUser)
                .lastActiveAt(Instant.now())
                .build();

        UserResponse expectedResponse = new UserResponse(
                userId,
                "user1",
                "user1@test.com",
                null,
                true
        );

        given(userRepository.existsByUsername("user1")).willReturn(false);
        given(userRepository.existsByEmail("user1@test.com")).willReturn(false);
        given(binaryContentService.createBinaryContent(profileRequest)).willReturn(profile);
        given(userMapper.toEntity(request, profile)).willReturn(user);
        given(userRepository.save(user)).willReturn(savedUser);
        given(userStatusMapper.toEntity(any(User.class), any(Instant.class))).willReturn(userStatus);
        given(userStatusRepository.save(userStatus)).willReturn(userStatus);
        given(userMapper.toResponse(savedUser, userStatus)).willReturn(expectedResponse);

        UserResponse result = userService.create(request, Optional.of(profileRequest));

        assertThat(result).isEqualTo(expectedResponse);

        then(binaryContentService).should().createBinaryContent(profileRequest);
        then(userMapper).should().toEntity(request, profile);
        then(userRepository).should().save(user);
        then(userStatusRepository).should().save(userStatus);
        then(userMapper).should().toResponse(savedUser, userStatus);
    }

    @Test
    @DisplayName("사용자 생성 실패 - username 중복")
    void create_fail_duplicateUsername() {
        UserCreateRequest request = new UserCreateRequest(
                "user1",
                "user1@test.com",
                "password"
        );

        given(userRepository.existsByUsername("user1")).willReturn(true);

        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
                .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().existsByUsername("user1");
        then(userRepository).should(never()).existsByEmail(any());
        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("사용자 생성 실패 - email 중복")
    void create_fail_duplicateEmail() {
        UserCreateRequest request = new UserCreateRequest(
                "user1",
                "user1@test.com",
                "password"
        );

        given(userRepository.existsByUsername("user1")).willReturn(false);
        given(userRepository.existsByEmail("user1@test.com")).willReturn(true);

        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
                .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().existsByUsername("user1");
        then(userRepository).should().existsByEmail("user1@test.com");
        then(userRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void update_success() {
        UUID userId = UUID.randomUUID();

        UserUpdateRequest request = new UserUpdateRequest(
                "newUser",
                "new@test.com",
                "newPassword"
        );

        User user = User.builder()
                .id(userId)
                .username("oldUser")
                .email("old@test.com")
                .password("oldPassword")
                .build();

        UserResponse expectedResponse = new UserResponse(
                userId,
                "newUser",
                "new@test.com",
                null,
                null
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByUsername("newUser")).willReturn(false);
        given(userRepository.existsByEmail("new@test.com")).willReturn(false);
        given(userMapper.toResponse(user)).willReturn(expectedResponse);

        UserResponse result = userService.update(userId, request, Optional.empty());

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(user.getUsername()).isEqualTo("newUser");
        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.getPassword()).isEqualTo("newPassword");

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByUsername("newUser");
        then(userRepository).should().existsByEmail("new@test.com");
        then(userMapper).should().toResponse(user);
    }

    @Test
    @DisplayName("사용자 수정 실패 - 사용자 없음")
    void update_fail_userNotFound() {
        UUID userId = UUID.randomUUID();

        UserUpdateRequest request = new UserUpdateRequest(
                "newUser",
                "new@test.com",
                "newPassword"
        );

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
                .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(userRepository).should(never()).existsByUsername(any());
        then(userRepository).should(never()).existsByEmail(any());
    }

    @Test
    @DisplayName("사용자 수정 실패 - email 중복")
    void update_fail_duplicateEmail() {
        UUID userId = UUID.randomUUID();

        UserUpdateRequest request = new UserUpdateRequest(
                null,
                "duplicate@test.com",
                null
        );

        User user = User.builder()
                .id(userId)
                .username("user1")
                .email("old@test.com")
                .password("password")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("duplicate@test.com")).willReturn(true);

        assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
                .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail("duplicate@test.com");
        then(userMapper).should(never()).toResponse(any(User.class));
    }

    @Test
    @DisplayName("사용자 삭제 성공 - 프로필 없음")
    void delete_success_withoutProfile() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        UserStatus userStatus = UserStatus.builder()
                .id(UUID.randomUUID())
                .user(user)
                .lastActiveAt(Instant.now())
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.of(userStatus));

        userService.delete(userId);

        then(userRepository).should().findById(userId);
        then(userStatusRepository).should().findByUser_Id(userId);
        then(userStatusRepository).should().delete(userStatus);
        then(userRepository).should().delete(user);
        then(binaryContentService).should(never()).delete(any());
    }

    @Test
    @DisplayName("사용자 삭제 실패 - 사용자 상태 없음")
    void delete_fail_userStatusNotFound() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@test.com")
                .password("password")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(UserStatusNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(userStatusRepository).should().findByUser_Id(userId);
        then(userRepository).should(never()).delete(any());
    }
}