package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.DuplicateUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;


  @Override
  public UserStatusDto create(UserStatusCreateRequest request) {
    log.info("UserStatus 생성 요청: userId={}", request.userId());

    UUID userId = request.userId();

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> {
          log.warn("UserStatus 생성 실패 - {}: userId={}", ErrorCode.USER_NOT_FOUND.getMessage(),
              request.userId());
          return new UserNotFoundException();
        });
    Optional.ofNullable(user.getStatus())
        .ifPresent(status -> {
          log.warn("UserStatus 생성 실패- {}: userId={}", ErrorCode.DUPLICATE_USER_STATUS.getMessage(),
              request.userId());
          throw new DuplicateUserStatusException();
        });

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    userStatusRepository.save(userStatus);
    log.info("UserStatus 생성 완료: id={}", userStatus.getId());
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
  }

  @Override
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    log.info("UserStatus 수정 요청: id={}", userStatusId);
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(
            () -> {
              log.warn("UserStatus 수정 실패 - {}: id={}", ErrorCode.USER_STATUS_NOT_FOUND.getMessage(),
                  userStatusId);
              return new UserStatusNotFoundException();
            });
    userStatus.update(newLastActiveAt);

    log.info("UserStatus 수정 완료: id={}", userStatusId);
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    log.info("UserStatus 수정 요청 (userId 기준): userId={}", userId);
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> {
              log.warn("UserStatus 수정 실패 - {}: userId={}",
                  ErrorCode.USER_STATUS_NOT_FOUND.getMessage(), userId);
              return new UserStatusNotFoundException();
            });
    userStatus.update(newLastActiveAt);
    log.info("UserStatus 수정 완료 (userId 기준): userId={}", userId);
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public void delete(UUID userStatusId) {
    log.info("UserStatus 삭제 요청: id={}", userStatusId);
    if (!userStatusRepository.existsById(userStatusId)) {
      log.warn("UserStatus 삭제 실패 - {}: id={}", ErrorCode.USER_STATUS_NOT_FOUND.getMessage(),
          userStatusId);
      throw new UserStatusNotFoundException();
    }
    userStatusRepository.deleteById(userStatusId);
    log.info("UserStatus 삭제 완료: id={}", userStatusId);
  }
}
