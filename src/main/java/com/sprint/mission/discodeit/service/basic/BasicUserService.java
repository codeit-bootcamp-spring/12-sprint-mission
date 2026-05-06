package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.domain.user.UserStatus;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(UserCreateRequest dto,
                               Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("Username already exists: " + dto.username());
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already exists: " + dto.email());
        }

        UUID nullableProfileId = optionalProfileCreateRequest
                .map(profileRequest -> {
                    BinaryContent binaryContent = new BinaryContent(
                            profileRequest.data(),
                            profileRequest.filename(),
                            profileRequest.mimeType());
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);

        User user = new User(
                nullableProfileId,
                dto.username(),
                dto.email(),
                dto.password());

        UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
        userRepository.save(user);
        userStatusRepository.save(userStatus);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProfileId(),
                userStatus.isOnline()
        );
    }

    @Override
    public UserResponse findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + userId));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProfileId(),
                userStatus.isOnline()
        );
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException(
                                    "No user status found for user id " + user.getId()
                            ));

                    return new UserResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getCreatedAt(),
                            user.getUpdatedAt(),
                            user.getProfileId(),
                            userStatus.isOnline()
                    );
                })
                .toList();
    }

    @Override
    public UserResponse update(UUID userId, UserUpdateRequest dto,
                               Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        if (dto.newUsername() != null
                && !dto.newUsername().equals(user.getUsername())
                && userRepository.existsByUsername(dto.newUsername())) {
            throw new IllegalArgumentException("Username already exists: " + dto.newUsername());
        }

        if (dto.newEmail() != null
                && !dto.newEmail().equals(user.getEmail())
                && userRepository.existsByEmail(dto.newEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.newEmail());
        }

        UUID profileId = user.getProfileId();

        if (optionalProfileCreateRequest.isPresent()) {
            BinaryContentCreateRequest request = optionalProfileCreateRequest.get();

            Optional.ofNullable(user.getProfileId())
                    .ifPresent(binaryContentRepository::deleteById);

            BinaryContent binaryContent = new BinaryContent(
                    request.data(),
                    request.filename(),
                    request.mimeType()
            );
            profileId = binaryContentRepository.save(binaryContent).getId();
        }

        user.update(
                dto.newUsername(),
                dto.newEmail(),
                dto.newPassword(),
                profileId
        );

        userRepository.save(user);

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + user.getId()));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProfileId(),
                userStatus.isOnline()
        );
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("No user status found for user id " + userId));

        userRepository.deleteById(user.getId());
        userStatusRepository.deleteById(userStatus.getId());

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
    }
}