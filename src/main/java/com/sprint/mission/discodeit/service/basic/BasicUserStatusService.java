package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.user.UserStatus;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest dto) {
        if (!userRepository.existsById(dto.userId())) {
            throw new NoSuchElementException("User not found with id " + dto.userId());
        }
        if (userStatusRepository.findByUserId(dto.userId()).isPresent()) {
            throw new IllegalStateException("User status already exists for user id " + dto.userId());
        }

        UserStatus userStatus = new UserStatus(dto.userId(), dto.lastActiveAt());
        userStatusRepository.save(userStatus);

        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.isOnline(),
                userStatus.getLastActiveAt(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt()
        );
    }

    @Override
    public UserStatusResponse findById(UUID id) {
        UserStatus userStatus = getUserStatusOrThrow(id);

        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.isOnline(),
                userStatus.getLastActiveAt(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt()
        );
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(status -> new UserStatusResponse(
                        status.getId(),
                        status.getUserId(),
                        status.isOnline(),
                        status.getLastActiveAt(),
                        status.getCreatedAt(),
                        status.getUpdatedAt()
                ))
                .toList();
    }

    @Override
    public UserStatusResponse update(UUID id, UserStatusUpdateRequest dto) {
        UserStatus userStatus = getUserStatusOrThrow(id);

        userStatus.update(dto.newLastActiveAt());
        userStatusRepository.save(userStatus);

        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.isOnline(),
                userStatus.getLastActiveAt(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt()
        );
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + userId));

        userStatus.update(dto.newLastActiveAt());
        userStatusRepository.save(userStatus);

        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.isOnline(),
                userStatus.getLastActiveAt(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt()
        );
    }

    @Override
    public void delete(UUID id) {
        getUserStatusOrThrow(id);
        userStatusRepository.deleteById(id);
    }

    private UserStatus getUserStatusOrThrow(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));
    }
}