package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 생성 시작: userCreateRequest={}, optionalProfileCreateRequestPresent={}",
        userCreateRequest, optionalProfileCreateRequest.isPresent());
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      log.warn("사용자 이메일 중복: email = {} ", email);
      throw UserAlreadyExistException.withEmail(email);
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("사용자 이름 중복: username = {} ", username);
      throw UserAlreadyExistException.withUsername(username);
    }

    BinaryContent nullableProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          BinaryContent saveBinaryContent = binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(saveBinaryContent.getId(), bytes);
          return saveBinaryContent;
        })
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfileId);
    User createdUser = userRepository.save(user);

    Instant now = Instant.now();
    UserStatus userStatus = UserStatus.builder()
        .user(createdUser)
        .lastActiveAt(now)
        .build();
    userStatusRepository.save(userStatus);
    createdUser.setUserStatus(userStatus);
    log.info("사용자 생성 완료: userId={}, username={}, email={}, profilePresent={}", createdUser.getId(),
        createdUser.getUsername(), createdUser.getEmail(), createdUser.getProfile() != null);
    return userMapper.toDto(createdUser);
  }

  @Override
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 시작: userId={}", userId);
    UserDto userDto = userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> {
          log.warn("사용자 조회 실패(사용자 없음): userId={}", userId);
          return UserNotFoundException.withId(userId);
        });
    log.info("사용자 조회 완료: userId={}, username={}, email={}, profilePresent={}", userId,
        userDto.username(), userDto.email(), userDto.profile() != null);
    return userDto;
  }

  @Override
  public List<UserDto> findAll() {
    log.debug("사용자 목록 조회 시작");
    List<UserDto> userDtos = userRepository.findAllWithProfileAndUserStatus()
        .stream()
        .map(userMapper::toDto)
        .toList();
    log.info("사용자 목록 조회 완료: userCount={}", userDtos.size());
    return userDtos;
  }

  @Override
  @Transactional
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자 수정 실패(사용자 없음): userId={}", userId);
          return UserNotFoundException.withId(userId);
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmail(newEmail)) {
      log.warn("사용자 이메일 중복: email = {} ", newEmail);
      throw UserAlreadyExistException.withEmail(newEmail);
    }
    if (userRepository.existsByUsername(newUsername)) {
      log.warn("사용자 이름 중복: username = {} ", newUsername);
      throw UserAlreadyExistException.withUsername(newUsername);
    }

    BinaryContent nullableProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          BinaryContent saveBinaryContent = binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(saveBinaryContent.getId(), bytes);
          Optional.ofNullable(user.getProfile().getId())
              .ifPresent(binaryContentRepository::deleteById);
          return saveBinaryContent;
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfileId);
    log.info("사용자 수정 완료: userId={}, newUsername={}, newEmail={}, newProfilePresent={}", userId,
        newUsername, newEmail, nullableProfileId != null);
    return userMapper.toDto(userRepository.save(user));
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시작: userId={}", userId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자 삭제 실패(사용자 없음): userId={}", userId);
          return UserNotFoundException.withId(userId);
        });
    if (user.getProfile() != null) {
      Optional.ofNullable(user.getProfile().getId())
          .ifPresent(binaryContentRepository::deleteById);
    }
    BinaryContent profile = user.getProfile();
    userStatusRepository.deleteByUserId(userId);
    if (profile != null) {
      binaryContentRepository.delete(profile);
    }
    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료: userId={}", userId);
  }
}
