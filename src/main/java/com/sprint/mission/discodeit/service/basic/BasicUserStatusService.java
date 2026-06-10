package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusResponse create(UserStatusCreateRequest request) {
        User user = getUserOrThrow(request.userId());

        if (userStatusRepository.findByUser_Id(request.userId()).isPresent()) {
            throw new UserStatusAlreadyExistsException(request.userId());
        }

        UserStatus userStatus = userStatusMapper.toEntity(user, request.lastActiveAt());
        UserStatus saved = userStatusRepository.save(userStatus);

        return userStatusMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatusResponse findById(UUID userStatusId) {
        UserStatus userStatus = getUserStatusByIdOrThrow(userStatusId);
        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
        UserStatus userStatus = getUserStatusByIdOrThrow(userStatusId);

        userStatus.updateLastActiveAt(request.newLastActiveAt());

        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    @Transactional
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        User user = getUserOrThrow(userId);

        UserStatus userStatus = userStatusRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> UserStatusNotFoundException.byUserId(user.getId()));

        userStatus.updateLastActiveAt(request.newLastActiveAt());

        return userStatusMapper.toResponse(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID userStatusId) {
        UserStatus userStatus = getUserStatusByIdOrThrow(userStatusId);
        userStatusRepository.delete(userStatus);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserStatus getUserStatusByIdOrThrow(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new UserStatusNotFoundException(userStatusId));
    }
}