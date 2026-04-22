package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
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


@Primary
@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;


    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        // User Confirmation
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User를 찾을 수 없습니다: " + request.getUserId()));

        // Duplicate Check
        userStatusRepository.findByUserId(request.getUserId()).ifPresent(existing -> {
            throw new IllegalArgumentException("이미 존재하는 UserStatus입니다: " + request.getUserId());
        });

        UserStatus userStatus = new UserStatus(
                UUID.randomUUID().toString(),
                request.getUserId().toString(),
                request.getLastReadAt()
        );
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id, UserStatusCreateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다: " + id));
        userStatus.updateLastSeenAt();
        return userStatusRepository.update(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다: " + userId));
        userStatus.updateLastSeenAt();
        return userStatusRepository.update(userStatus);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다: " + id));
        userStatusRepository.deleteById(id);
    }
}
