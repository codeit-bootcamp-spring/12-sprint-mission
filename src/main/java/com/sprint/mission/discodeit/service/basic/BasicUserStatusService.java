package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.data.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.data.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        boolean isDuplicate = userStatusRepository.findAll().stream()
                .anyMatch(us -> us.getUserId().equals(request.userId()));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 해당 유저의 상태 정보가 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(request.userId());
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusResponse find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));
        return toResponse(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatusResponse update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));

        userStatus.updateLastActiveAt();
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 유저의 상태 정보를 찾을 수 없습니다."));

        userStatus.updateLastActiveAt();
        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("UserStatus not found");
        }
        userStatusRepository.deleteById(id);
    }

    private UserStatusResponse toResponse(UserStatus userStatus) {
        return UserStatusResponse.builder()
                .id(userStatus.getId())
                .userId(userStatus.getUserId())
                .lastActiveAt(userStatus.getLastActiveAt())
                .isOnline(userStatus.isOnline())
                .createdAt(userStatus.getCreatedAt())
                .updatedAt(userStatus.getUpdatedAt())
                .build();
    }
}