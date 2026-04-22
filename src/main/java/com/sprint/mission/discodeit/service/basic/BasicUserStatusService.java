package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.user.User;
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
                userStatus.getLastActiveAt(),
                userStatus.getCreatedAt()
        );
    }

    @Override
    public UserStatusResponse findById(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));

        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastActiveAt(),
                userStatus.getCreatedAt()
        );
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(status -> new UserStatusResponse(
                        status.getId(),
                        status.getUserId(),
                        status.getLastActiveAt(),
                        status.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public UserStatusResponse update(UUID id, UserStatusUpdateRequest dto) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + id + " not found"));

        userStatus.update(dto.newLastActiveAt());
        userStatusRepository.save(userStatus);

        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastActiveAt(),
                userStatus.getCreatedAt()
        );
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + user.getId()));

        userStatus.update(dto.newLastActiveAt());
        userStatusRepository.save(userStatus);

        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastActiveAt(),
                userStatus.getCreatedAt()
        );
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        userStatusRepository.deleteById(id);
    }
}