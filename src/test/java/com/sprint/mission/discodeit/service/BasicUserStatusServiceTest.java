package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.DuplicateUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
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

@ExtendWith(MockitoExtension.class)
class BasicUserStatusServiceTest {

  @Mock UserStatusRepository userStatusRepository;
  @Mock UserRepository userRepository;
  @Mock UserStatusMapper userStatusMapper;

  @InjectMocks BasicUserStatusService userStatusService;

  private UUID userId;
  private UUID userStatusId;
  private User user;
  private UserStatus userStatus;
  private UserStatusDto userStatusDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    userStatusId = UUID.randomUUID();
    user = new User("testuser", "test@email.com", "password123!", null);
    userStatus = new UserStatus(user, Instant.now());
    userStatusDto = new UserStatusDto(userStatusId, userId, Instant.now());
  }

  @Test
  @DisplayName("UserStatus 생성 성공")
  void create_success() {
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.empty());
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
    given(userStatusMapper.toDto(userStatus)).willReturn(userStatusDto);

    UserStatusDto result = userStatusService.create(new UserStatusCreateRequest(userId, Instant.now()));

    assertThat(result).isNotNull();
    then(userStatusRepository).should().save(any(UserStatus.class));
  }

  @Test
  @DisplayName("존재하지 않는 유저로 UserStatus 생성 시 예외 발생")
  void create_userNotFound_throwsException() {
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userStatusService.create(new UserStatusCreateRequest(userId, Instant.now())))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("이미 UserStatus가 있는 유저에 생성 시 예외 발생")
  void create_duplicate_throwsException() {
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.of(userStatus));

    assertThatThrownBy(() -> userStatusService.create(new UserStatusCreateRequest(userId, Instant.now())))
        .isInstanceOf(DuplicateUserStatusException.class);
  }

  @Test
  @DisplayName("UserStatus 단건 조회 성공")
  void find_success() {
    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(userStatus)).willReturn(userStatusDto);

    UserStatusDto result = userStatusService.find(userStatusId);

    assertThat(result).isEqualTo(userStatusDto);
  }

  @Test
  @DisplayName("존재하지 않는 UserStatus 조회 시 예외 발생")
  void find_notFound_throwsException() {
    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userStatusService.find(userStatusId))
        .isInstanceOf(UserStatusNotFoundException.class);
  }

  @Test
  @DisplayName("전체 UserStatus 조회 성공")
  void findAll_success() {
    given(userStatusRepository.findAll()).willReturn(List.of(userStatus));
    given(userStatusMapper.toDto(userStatus)).willReturn(userStatusDto);

    List<UserStatusDto> result = userStatusService.findAll();

    assertThat(result).hasSize(1);
  }

  @Test
  @DisplayName("UserStatus 수정 성공")
  void update_success() {
    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));
    given(userStatusRepository.save(userStatus)).willReturn(userStatus);
    given(userStatusMapper.toDto(userStatus)).willReturn(userStatusDto);

    UserStatusDto result = userStatusService.update(userStatusId, new UserStatusUpdateRequest(Instant.now()));

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("userId로 UserStatus 수정 성공")
  void updateByUserId_success() {
    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.of(userStatus));
    given(userStatusRepository.save(userStatus)).willReturn(userStatus);
    given(userStatusMapper.toDto(userStatus)).willReturn(userStatusDto);

    UserStatusDto result = userStatusService.updateByUserId(userId, new UserStatusUpdateRequest(Instant.now()));

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("UserStatus 삭제 성공")
  void delete_success() {
    given(userStatusRepository.existsById(userStatusId)).willReturn(true);

    userStatusService.delete(userStatusId);

    then(userStatusRepository).should().deleteById(userStatusId);
  }

  @Test
  @DisplayName("존재하지 않는 UserStatus 삭제 시 예외 발생")
  void delete_notFound_throwsException() {
    given(userStatusRepository.existsById(userStatusId)).willReturn(false);

    assertThatThrownBy(() -> userStatusService.delete(userStatusId))
        .isInstanceOf(UserStatusNotFoundException.class);
  }
}
