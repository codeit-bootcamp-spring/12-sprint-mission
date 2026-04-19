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

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        User user = userRepository.findById(request.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        UserStatus existing = userStatusRepository.findByUserId(request.getUserId());
        if (existing != null) {
            throw new IllegalStateException("이미 해당 유저의 UserStatus가 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(request.getUserId());
        userStatusRepository.save(userStatus);
        return userStatus;
    }

    @Override
    public UserStatus findById(UUID id) {
        return userStatusRepository.findById(id);
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id, UserStatusUpdateRequest request) {
        UserStatus target = userStatusRepository.findById(id);

        if (target == null) {
            throw new IllegalArgumentException("UserStatus가 존재하지 않습니다.");
        }

        target.updateLastSeen();
        userStatusRepository.save(target);
        return target;
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus target = userStatusRepository.findByUserId(userId);

        if (target == null) {
            throw new IllegalArgumentException("해당 유저의 UserStatus가 존재하지 않습니다.");
        }

        target.updateLastSeen();
        userStatusRepository.save(target);
        return target;
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.delete(id);
    }
}