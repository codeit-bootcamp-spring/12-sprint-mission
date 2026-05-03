package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public User create(UserCreateRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException(
          "User with Username " + request.username() + " already exists");
    }

    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("User with Email " + request.email() + " already exists");
    }

    User user = request.toUser();
    UserStatus status = new UserStatus(user.getId(), Instant.MIN);
    userStatusRepository.save(status);

    return userRepository.save(user);
  }

  @Override
  public UserDto find(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException(
            "User with id " + userId + " not found (UserService-find)"));

    UserStatus status = userStatusRepository.findByUserId(user.getId())
        .orElseThrow(() -> new NoSuchElementException(
            "UserStatus for user id " + userId + " not found (UserService-find)"));

    return UserDto.from(user, status);
  }

  @Override
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAll();
    List<UserStatus> statuses = userStatusRepository.findAll();

    Map<UUID, UserStatus> statusMap = statuses.stream()
        .collect(Collectors.toMap(UserStatus::getUserId, status -> status));

    return users.stream()
        .map(user -> {
          UserStatus status = statusMap.getOrDefault(user.getId(), new UserStatus());
          return UserDto.from(user, status);
        })
        .toList();
  }

  @Override
  public UserDto update(UserUpdateRequest request) {
    User user = userRepository.findById(request.id())
        .orElseThrow(() -> new NoSuchElementException(
            "User with id " + request.id() + " not found (UserService-update)"));

    // 변경하려는 이메일이 본인의 기존 이메일과 다를 때만 중복 검사 수행
    if (request.newEmail() != null && !request.newEmail().equals(user.getEmail())
        && userRepository.existsByEmail(request.newEmail())) {
      throw new IllegalArgumentException(
          "User with Email " + request.newEmail() + " already exists");
    }

    // 변경하려는 유저네임이 본인의 기존 유저네임과 다를 때만 중복 검사 수행
    if (request.newUsername() != null && !request.newUsername().equals(user.getUsername())
        && userRepository.existsByUsername(request.newUsername())) {
      throw new IllegalArgumentException(
          "User with Username " + request.newUsername() + " already exists");
    }

    if (request.profileImageId() != null && !request.profileImageId().equals(user.getProfileId())) {
      if (user.getProfileId() != null) {
        binaryContentRepository.delete(user.getProfileId());
      }
    }

    user.update(
        request.newUsername() != null ? request.newUsername() : user.getUsername(),
        request.newEmail() != null ? request.newEmail() : user.getEmail(),
        request.newPassword() != null ? request.newPassword() : user.getPassword(),
        request.profileImageId() != null ? request.profileImageId() : user.getProfileId()
    );

    userRepository.save(user);

    UserStatus status = userStatusRepository.findByUserId(request.id())
        .orElse(new UserStatus());

    return UserDto.from(user, status);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException(
            "User with id " + userId + " not found (UserService-delete)"));

    userStatusRepository.deleteByUserId(userId);

    UUID profileImageId = user.getProfileId();
    if (profileImageId != null) {
      binaryContentRepository.delete(profileImageId);
    }

    userRepository.deleteById(userId);
  }
}
