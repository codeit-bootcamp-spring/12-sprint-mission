package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("email already exists");
        }
        UUID profileId = saveProfileIfExists(request.profileImage());
        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profileId
        );
        User savedUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(savedUser.getId(), Instant.now());
        userStatusRepository.save(userStatus);
        return toResponse(savedUser);
    }

    @Override
    public UserResponse findById(UUID userId) {
        return toResponse(getUser(userId));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = getUser(request.userId());

        if (request.newUsername() != null
                && !request.newUsername().equals(user.getUsername())
                && userRepository.existsByUsername(request.newUsername())) {
            throw new IllegalArgumentException("username already exists");
        }
        if (request.newEmail() != null
                && !request.newEmail().equals(user.getEmail())
                && userRepository.existsByEmail(request.newEmail())) {
            throw new IllegalArgumentException("email already exists");
        }
        UUID newProfileId = null;
        if (request.newProfileImage() != null) {
            newProfileId = saveProfileIfExists(request.newProfileImage());
            if (user.getProfileId() != null) {
                binaryContentRepository.deleteById(user.getProfileId());
            }
        }

        user.update(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                newProfileId
        );
        return toResponse(userRepository.save(user));
    }

    @Override
    public void delete(UUID userId) {
        User user = getUser(userId);
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        if (userStatusRepository.existsById(userId)) {
            userStatusRepository.deleteById(userId);
        }
        userRepository.deleteById(userId);
    }

    private UUID saveProfileIfExists(BinaryContentCreateRequest request) {
        if (request == null) {
            return null;
        }
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
        return binaryContentRepository.save(binaryContent).getId();
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found : " + userId));
    }

    private UserResponse toResponse(User user) {
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                userStatus != null && userStatus.isOnline(),
                userStatus != null ? userStatus.getLastSeenAt() : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
