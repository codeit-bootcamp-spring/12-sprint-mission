package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public UserResponse create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw UserAlreadyExistsException.withEmail(email);
    }
    if (userRepository.existsByUsername(username)) {
      throw UserAlreadyExistsException.withUsername(username);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          BinaryContent binaryContent = new BinaryContent(
              profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType()
          );
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
          return binaryContent;
        })
        .orElse(null);

    User user = new User(username, email, userCreateRequest.password(), nullableProfile);
    UserStatus userStatus = new UserStatus(user, Instant.now());

    userRepository.save(user);
    userStatusRepository.save(userStatus); // 누락 수정
    return userMapper.toResponse(user);
  }

  @Override
  public UserResponse findById(UUID id) {
    return userRepository.findById(id)
        .map(userMapper::toResponse)
        .orElseThrow(
            () -> new NoSuchElementException("User with id " + id + " not found")); // null 반환 수정
  }

  @Override
  public List<UserResponse> findAll() {
    return userRepository.findAllWithProfileAndStatus()
        .stream()
        .map(userMapper::toResponse)
        .toList();
  }

  @Transactional
  @Override
  public UserResponse update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          BinaryContent binaryContent = new BinaryContent(
              profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType()
          );
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
          return binaryContent;
        })
        .orElse(null);

    user.update(newUsername, newEmail, userUpdateRequest.newPassword(), nullableProfile);
    return userMapper.toResponse(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    if (!userRepository.existsById(userId)) { // 조건 반전 수정
      throw UserNotFoundException.withId(userId);
    }
    userRepository.deleteById(userId);
  }
}