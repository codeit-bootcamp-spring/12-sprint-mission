package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.data.request.user.UserStatusCreateRequest;
import com.sprint.mission.discodeit.data.request.user.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    public UserStatus create(UserStatusCreateRequest request) {
        if (request.userId() == null) {
            throw new IllegalArgumentException("유저 아이디 없음");
        }

        if (userRepository.findById(request.userId()).isEmpty()) {
            throw new IllegalArgumentException("저장소에 유저 아이디 없음");
        }

        if (userStatusRepository.findByUserId(request.userId()).isPresent()) {
            throw new IllegalArgumentException("해당 유저의 UserStatus가 이미 존재함");
        }

        return userStatusRepository.save(new UserStatus(request.userId()));
    }

    public UserStatus find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id).orElse(null);

        if (userStatus == null) {
            throw new IllegalArgumentException("UserStatus 없음");
        }

        return userStatus;
    }

    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElse(null);

        if (userStatus == null) {
            throw new IllegalArgumentException("userStatus 없음");
        }
        userStatus.update();

        return userStatus;
    }

    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    public UserStatus update(UUID id, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(id).orElse(null);

        if (userStatus == null) {
            throw new IllegalArgumentException("UserStatus 없음");
        }

        userStatus.update();

        return userStatusRepository.save(userStatus);
    }

    public UserStatus delete(UUID id) {
        return userStatusRepository.deleteById(id);
    }
}