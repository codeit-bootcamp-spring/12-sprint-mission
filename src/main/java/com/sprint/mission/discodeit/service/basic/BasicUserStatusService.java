package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("userStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User with id " + request.userId() + " not found (UserStatusService-create)"));

        if (userStatusRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalArgumentException("UserStatus for user id " + request.userId() + " already exists (UserStatusService-create)");
        }

        return userStatusRepository.save(new UserStatus(user.getId()));
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus with id " + id + " not found (UserStatusService-find)"));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("UserStatus with id " + request.id() + " not found (UserStatusService-update)"));

        userStatus.updateTimestamp();

        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus for user id " + userId + " not found (UserStatusService-updateByUserId)"));

        userStatus.updateTimestamp();

        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus with id " + id + " not found (UserStatusService-delete)"));

        userStatusRepository.delete(userStatus.getId());
    }
}
