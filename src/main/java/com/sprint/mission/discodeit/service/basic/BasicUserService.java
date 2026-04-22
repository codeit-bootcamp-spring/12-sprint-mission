package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserDto create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exises.");
        }

        UUID profileId = null;

        if (request.profile() != null) {
            BinaryContent profile = new BinaryContent(
                    UUID.randomUUID(),
                    request.profile().fileName(),
                    request.profile().contentType(),
                    request.profile().bytes()
            );
            BinaryContent savedProfile = binaryContentRepository.save(profile);
            profileId = savedProfile.getId();
        }

        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profileId
        );

        User savedUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(
                UUID.randomUUID(),
                savedUser.getId()
        );

        userStatusRepository.save(userStatus);

        return new UserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                userStatus.isOnline()
        );
    }

    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                online
        );
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean online = userStatusRepository.findByUserId(user.getId())
                    .map(UserStatus::isOnline)
                            .orElse(false);

                    return new UserDto(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            online
                    );
                })
                .toList();
    }

    @Override
    public UserDto update(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        if (!user.getUsername().equals(request.username())
        && userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (!user.getEmail().equals(request.email())
        && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        UUID profileId = user.getProfileId();

        if (request.profile() != null) {
            BinaryContent profile = new BinaryContent(
                    UUID.randomUUID(),
                    request.profile().fileName(),
                    request.profile().contentType(),
                    request.profile().bytes()
            );
            BinaryContent savedProfile = binaryContentRepository.save(profile);
            profileId = savedProfile.getId();
        }

        user.update(
                request.username(),
                request.email(),
                request.password(),
                profileId
        );

        User savedUser = userRepository.save(user);

        boolean online = userStatusRepository.findByUserId(savedUser.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                online
        );
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}