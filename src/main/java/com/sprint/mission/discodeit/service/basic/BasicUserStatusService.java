package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userStatus.UpdateUserStatusRequest;
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
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(CreateUserStatusRequest request) {
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User with id " + request.userId() + " not found");
        }

        userStatusRepository.findByUserId(request.userId())
                .ifPresent(userStatus -> {
                    throw new IllegalArgumentException(
                            "UserStatus already exists for userId " + request.userId()
                    );
                });

        UserStatus userStatus = new UserStatus(
                UUID.randomUUID(),
                request.userId()
        );

        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus with id " + userStatusId + " not found"
                ));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UpdateUserStatusRequest request) {
        UserStatus userStatus = userStatusRepository.findById(request.userStatusId())
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus with id " + request.userStatusId() + " not found"
                ));

        userStatus.update();
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus with userId " + userId + " not found"
                ));

        userStatus.update();
        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus with id " + userStatusId + " not found"
                ));

        userStatusRepository.deleteById(userStatusId);
    }
}