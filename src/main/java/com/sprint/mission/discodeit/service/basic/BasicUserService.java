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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public UserResponse create(UserCreateRequest dto, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        boolean existsUsername = userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(dto.username()));
        if (existsUsername) {
            throw new IllegalArgumentException("Username already exists: " + dto.username());
        }

        boolean existsEmail = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(dto.email()));
        if (existsEmail) {
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
        userStatusRepository.save(userStatus);
        userRepository.save(user);

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
    public UserResponse update(UUID userId, UserUpdateRequest dto, MultipartFile profile) {
        if (dto.newUsername() != null) {
            boolean existsUsername = userRepository.findAll().stream()
                    .anyMatch(user ->
                            !user.getId().equals(userId)
                                    && user.getUsername().equals(dto.newUsername())
                    );
            if (existsUsername) {
                throw new IllegalArgumentException("Username already exists: " + dto.newUsername());
            }
        }

        if (dto.newEmail() != null) {
            boolean existsEmail = userRepository.findAll().stream()
                    .anyMatch(user ->
                            !user.getId().equals(userId)
                                    && user.getEmail().equals(dto.newEmail())
                    );
            if (existsEmail) {
                throw new IllegalArgumentException("Email already exists: " + dto.newEmail());
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UUID profileId = user.getProfileId();

        if (profile != null && !profile.isEmpty()) {
            try {
                Optional.ofNullable(user.getProfileId())
                        .ifPresent(binaryContentRepository::deleteById);

                BinaryContent binaryContent = new BinaryContent(
                        profile.getBytes(),
                        profile.getOriginalFilename(),
                        profile.getContentType()
                );
                profileId = binaryContentRepository.save(binaryContent).getId();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read profile file", e);
            }
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