package com.sprint.mission.discodeit.service.basic;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.github.f4b6a3.uuid.UuidCreator;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusNotFoundException;
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
public class BasicUserStatusServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserStatusMapper userStatusMapper;

  @InjectMocks
  private BasicUserStatusService userStatusService;

  private UUID userId;
  private UUID userStatusId;
  private Instant lastActiveAt;
  private String username;
  private String password;
  private String email;
  private User user;
  private UserStatus userStatus;
  private UserStatusDto userStatusDto;

  @BeforeEach
  public void setUp() {
    userId = UuidCreator.getTimeOrderedEpoch();
    userStatusId = UuidCreator.getTimeOrderedEpoch();
    username = "testUser";
    password = "password1234!";
    email = "test@test.com";
    user = new User(username, email, password, null);
    ReflectionTestUtils.setField(user, "id", userId);
    lastActiveAt = Instant.now().minusSeconds(5);
    userStatus = UserStatus.builder()
        .user(user)
        .lastActiveAt(lastActiveAt)
        .build();
    ReflectionTestUtils.setField(userStatus, "id", userStatusId);
    userStatusDto = new UserStatusDto(userStatusId, userId, lastActiveAt);
  }

  @Test
  @DisplayName("사용자 상태 생성 테스트(성공)")
  public void create_success() {
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, lastActiveAt);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    UserStatusDto result = userStatusService.create(request);

    assertThat(result).isEqualTo(userStatusDto);
    verify(userStatusRepository).findByUserId(any());
  }

  @Test
  @DisplayName("사용자 상태 생성 테스트(실패 - 사용자 없음)")
  public void create_fail_not_found() {
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, lastActiveAt);

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userStatusService.create(request))
        .isInstanceOf(UserNotFoundException.class);
    verify(userRepository).findById(any());
    verify(userStatusRepository, never()).findByUserId(any());
    verify(userStatusRepository, never()).save(any());
  }

  @Test
  @DisplayName("사용자 상태 생성 테스트(실패 - 이미 존재)")
  public void create_fail_already_exists() {
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, lastActiveAt);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));

    assertThatThrownBy(() -> userStatusService.create(request))
        .isInstanceOf(UserStatusAlreadyExistException.class);
    verify(userRepository).findById(any());
    verify(userStatusRepository).findByUserId(any());
    verify(userStatusRepository, never()).save(any());
  }

  @Test
  @DisplayName("사용자 상태 조회 테스트(성공)")
  public void find_success() {
    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    UserStatusDto result = userStatusService.find(userStatusId);

    assertThat(result).isEqualTo(userStatusDto);
  }

  @Test
  @DisplayName("사용자 상태 조회 테스트(실패 - 상태 없음)")
  public void find_fail_not_found() {
    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userStatusService.find(userStatusId))
        .isInstanceOf(UserStatusNotFoundException.class);
    verify(userStatusRepository).findById(any());
  }

  @Test
  @DisplayName("모든 사용자 상태 조회 테스트(성공)")
  public void findAll_success() {
    List<UserStatus> userStatusList = List.of(userStatus);
    given(userStatusRepository.findAll()).willReturn(userStatusList);
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    List<UserStatusDto> result = userStatusService.findAll();

    assertThat(result).isEqualTo(List.of(userStatusDto));
  }

  @Test
  @DisplayName("사용자 상태 수정 테스트(성공)")
  public void update_success() {
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    UserStatusDto result = userStatusService.update(userStatusId, request);

    assertThat(result).isEqualTo(userStatusDto);
    verify(userStatusRepository).findById(any());
    verify(userStatusRepository).save(any());
  }

  @Test
  @DisplayName("사용자 상태 수정 테스트(실패 - 상태 없음)")
  public void update_fail_not_found() {
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userStatusService.update(userStatusId, request))
        .isInstanceOf(UserStatusNotFoundException.class);
    verify(userStatusRepository).findById(any());
    verify(userStatusRepository, never()).save(any());
  }

  @Test
  @DisplayName("사용자 ID로 사용자 상태 수정 테스트(성공)")
  public void updateByUserId_success_findByUserId() {
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    UserStatusDto result = userStatusService.updateByUserId(userId, request);

    assertThat(result).isEqualTo(userStatusDto);
  }

  @Test
  @DisplayName("사용자 ID로 사용자 상태 수정 테스트(실패 - 상태 없음)")
  public void updateByUserId_fail_not_found() {
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());
    assertThatThrownBy(() -> userStatusService.updateByUserId(userId, request))
        .isInstanceOf(UserStatusNotFoundException.class);
    verify(userStatusRepository).findByUserId(any());
    verify(userStatusRepository, never()).save(any());
  }

  @Test
  @DisplayName("사용자 상태 삭제 테스트(성공)")
  public void delete_success() {
    given(userStatusRepository.existsById(userStatusId)).willReturn(true);
    userStatusService.delete(userStatusId);
    verify(userStatusRepository).deleteById(userStatusId);
  }

  @Test
  @DisplayName("사용자 상태 삭제 테스트(실패 - 상태 없음)")
  public void delete_fail_not_found() {
    given(userStatusRepository.existsById(userStatusId)).willReturn(false);
    assertThatThrownBy(() -> userStatusService.delete(userStatusId))
        .isInstanceOf(UserStatusNotFoundException.class);
    verify(userStatusRepository, never()).deleteById(userStatusId);
  }
}
