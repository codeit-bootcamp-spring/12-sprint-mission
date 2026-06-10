package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.DuplicateUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicUserStatusServiceTest {

  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusMapper userStatusMapper;

  @InjectMocks
  private BasicUserStatusService userStatusService;

  private UUID userStatusId;
  private UUID userId;
  private User user;
  private UserStatus userStatus;
  private UserStatusDto userStatusDto;

  @BeforeEach
  void setUp() {
    userStatusId = UUID.randomUUID();
    userId = UUID.randomUUID();

    user = new User("testUser", "test@example.com", "test1234", null);
    ReflectionTestUtils.setField(user, "id", userId);

    userStatus = new UserStatus(user, Instant.now());
    ReflectionTestUtils.setField(userStatus, "id", userStatusId);

    userStatusDto = new UserStatusDto(userStatusId, userId, Instant.now());
  }

  // ── create ──────────────────────────────────────────────

  @Test
  @DisplayName("UserStatus 생성 성공")
  void createUserStatus_Success() {
    // given
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());
    User freshUser = new User("testUser", "test@example.com", "test1234", null);
    ReflectionTestUtils.setField(freshUser, "id", userId);

    given(userRepository.findById(eq(userId))).willReturn(Optional.of(freshUser));
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    // when
    UserStatusDto result = userStatusService.create(request);

    // then
    assertThat(result.userId()).isEqualTo(userId);
    verify(userStatusRepository).save(any(UserStatus.class));
  }

  @Test
  @DisplayName("존재하지 않는 유저로 UserStatus 생성 시 실패")
  void createUserStatus_UserNotFound_ThrowsException() {
    // given
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());
    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userStatusService.create(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("이미 UserStatus가 있는 유저로 생성 시 실패")
  void createUserStatus_DuplicateStatus_ThrowsException() {
    // given
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));

    // when & then
    assertThatThrownBy(() -> userStatusService.create(request))
        .isInstanceOf(DuplicateUserStatusException.class);
  }

  // ── update ──────────────────────────────────────────────

  @Test
  @DisplayName("UserStatus 수정 성공")
  void updateUserStatus_Success() {
    // given
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    given(userStatusRepository.findById(eq(userStatusId))).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    // when
    UserStatusDto result = userStatusService.update(userStatusId, request);

    // then
    assertThat(result).isEqualTo(userStatusDto);
  }

  @Test
  @DisplayName("존재하지 않는 UserStatus 수정 시 실패")
  void updateUserStatus_NotFound_ThrowsException() {
    // given
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    given(userStatusRepository.findById(eq(userStatusId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userStatusService.update(userStatusId, request))
        .isInstanceOf(UserStatusNotFoundException.class);
  }

  // ── updateByUserId ───────────────────────────────────────

  @Test
  @DisplayName("userId 기준 UserStatus 수정 성공")
  void updateByUserId_Success() {
    // given
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    given(userStatusRepository.findByUserId(eq(userId))).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    // when
    UserStatusDto result = userStatusService.updateByUserId(userId, request);

    // then
    assertThat(result).isEqualTo(userStatusDto);
  }

  @Test
  @DisplayName("존재하지 않는 userId로 UserStatus 수정 시 실패")
  void updateByUserId_NotFound_ThrowsException() {
    // given
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    given(userStatusRepository.findByUserId(eq(userId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userStatusService.updateByUserId(userId, request))
        .isInstanceOf(UserStatusNotFoundException.class);
  }

  // ── delete ──────────────────────────────────────────────

  @Test
  @DisplayName("UserStatus 삭제 성공")
  void deleteUserStatus_Success() {
    // given
    given(userStatusRepository.existsById(eq(userStatusId))).willReturn(true);

    // when
    userStatusService.delete(userStatusId);

    // then
    verify(userStatusRepository).deleteById(userStatusId);
  }

  @Test
  @DisplayName("존재하지 않는 UserStatus 삭제 시 실패")
  void deleteUserStatus_NotFound_ThrowsException() {
    // given
    given(userStatusRepository.existsById(eq(userStatusId))).willReturn(false);

    // when & then
    assertThatThrownBy(() -> userStatusService.delete(userStatusId))
        .isInstanceOf(UserStatusNotFoundException.class);
  }

  // ── findAll ──────────────────────────────────────────────

  @Test
  @DisplayName("UserStatus 전체 조회 성공")
  void findAllUserStatus_Success() {
    // given
    given(userStatusRepository.findAll()).willReturn(List.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    // when
    List<UserStatusDto> result = userStatusService.findAll();

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).userId()).isEqualTo(userId);
  }
}