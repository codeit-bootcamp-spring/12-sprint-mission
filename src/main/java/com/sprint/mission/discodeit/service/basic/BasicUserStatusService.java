package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusDuplicateException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFound;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public UserStatusResponse create(UserStatusCreateRequest request) {
    User user = getUserOrThrow(request.userId());
    validateUserStatusNotExists(request.userId());

    UserStatus userStatus = new UserStatus(user);
    UserStatus savedUserStatus = userStatusRepository.save(userStatus);

    return UserStatusResponse.from(savedUserStatus);
  }

  @Override
  public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    UserStatus userStatus = getUserStatusByUserIdOrThrow(userId);

    userStatus.updateLastActiveAt(request.newLastActiveAt());
    UserStatus updatedUserStatus = userStatusRepository.save(userStatus);

    return UserStatusResponse.from(updatedUserStatus);
  }

  @Override
  public void delete(UUID id) {
    getUserStatusOrThrow(id);
    userStatusRepository.deleteById(id);
  }

  private UserStatus getUserStatusOrThrow(UUID id) {
    return userStatusRepository.findById(id).orElseThrow(
        () -> new UserStatusNotFound(id)
    );
  }

  private UserStatus getUserStatusByUserIdOrThrow(UUID userId) {
    return userStatusRepository.findByUserId(userId).orElseThrow(
        () -> UserStatusNotFound.withUserId(userId)
    );
  }

  private User getUserOrThrow(UUID userId) {
    return userRepository.findById(userId).orElseThrow(
        () -> new UserNotFoundException(userId)
    );
  }

  private void validateUserStatusNotExists(UUID userId) {
    userStatusRepository.findByUserId(userId).ifPresent(userStatus -> {
      throw UserStatusDuplicateException.withUserId(userId);
    });
  }

}
