package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User not found : " + request.userId());
        }
        if (userStatusRepository.existsById(request.userId())) {
            throw new IllegalArgumentException("UserStatus already exists for user");
        }
        UserStatus userStatus = new UserStatus(
                request.userId(),
                request.lastSeenAt()
        );
        return toResponse(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusResponse findById(UUID id) {
        return toResponse(getUserStatus(id));
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateRequest request) {
        UserStatus userStatus = getUserStatus(request.userStatusId());
        userStatus.updateLastSeenAt(request.newLastSeenAt());
        return toResponse(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusResponse updateByUserId(UserStatusUpdateByUserIdRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(request.userId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for user : " + request.userId()));
        userStatus.updateLastSeenAt(request.newLastSeenAt());
        return toResponse(userStatusRepository.save(userStatus));
    }

    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("UserStatus not found : " + id);
        }
        userStatusRepository.deleteById(id);
    }

    private UserStatus getUserStatus(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found : " + id));
    }

    private UserStatusResponse toResponse(UserStatus userStatus) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getLastSeenAt(),
                userStatus.isOnline(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt()
        );
    }
}
