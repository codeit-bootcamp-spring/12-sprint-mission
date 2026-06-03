package com.sprint.mission.discodeit.service.basic;

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
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional
  public UserStatusDto create(UserStatusCreateRequest request) {
    log.debug("UserStatus 생성 시작: request={}", request);
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> {
              log.warn("UserStatus 생성 실패(사용자 없음): userId={}", userId);
              return UserNotFoundException.withId(userId);
            });

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      log.warn("UserStatus 생성 실패(이미 존재): userId={}", userId);
      throw UserStatusAlreadyExistException.withUserId(userId);
    }

    UserStatus userStatus = UserStatus.builder()
        .user(user)
        .lastActiveAt(request.lastActiveAt())
        .build();
    UserStatus saved = userStatusRepository.save(userStatus);
    log.info("UserStatus 생성 성공: userId={}", userId);
    return userStatusMapper.toDto(saved);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    log.debug("UserStatus 조회 시작: userStatusId={}", userStatusId);
    UserStatusDto userStatusDto = userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(
            () -> {
              log.warn("UserStatus 조회 실패(UserStatus 없음): userStatusId={}", userStatusId);
              return UserNotFoundException.withId(userStatusId);
            });
    log.info("UserStatus 조회 성공: userStatusId={}", userStatusId);
    return userStatusDto;
  }

  @Override
  public List<UserStatusDto> findAll() {
    log.debug("모든 UserStatus 조회 시작");
    List<UserStatusDto> userStatusDtos = userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
    log.info("모든 UserStatus 조회 성공: count={}", userStatusDtos.size());
    return userStatusDtos;
  }

  @Override
  @Transactional
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    log.debug("UserStatus 업데이트 시작: userStatusId={}, request={}", userStatusId, request);
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(
            () -> {
              log.warn("UserStatus 업데이트 실패(UserStatus 없음): userStatusId={}", userStatusId);
              return UserStatusNotFoundException.withId(userStatusId);
            });
    userStatus.update(newLastActiveAt);
    log.info("UserStatus 업데이트 성공: userStatusId={}, newLastActiveAt={}", userStatusId,
        newLastActiveAt);
    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    log.debug("UserStatus 업데이트 시작: userId={}, request={}", userId, request);
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> {
              log.warn("UserStatus 업데이트 실패(사용자의 UserStatus 없음): userId={}", userId);
              return UserStatusNotFoundException.withId(userId);
            });
    userStatus.update(newLastActiveAt);
    log.info("UserStatus 업데이트 성공: userId={}, newLastActiveAt={}", userId, newLastActiveAt);
    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public void delete(UUID userStatusId) {
    log.debug("UserStatus 삭제 시작: userStatusId={}", userStatusId);
    if (!userStatusRepository.existsById(userStatusId)) {
      log.warn("UserStatus 삭제 실패(UserStatus 없음): userStatusId={}", userStatusId);
      throw UserStatusNotFoundException.withId(userStatusId);
    }
    userStatusRepository.deleteById(userStatusId);
    log.info("UserStatus 삭제 성공: userStatusId={}", userStatusId);
  }
}
