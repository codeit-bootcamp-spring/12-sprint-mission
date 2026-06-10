package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;
    private final BinaryContentService binaryContentService;

    @Override
    @Transactional
    public UserResponse create(
            UserCreateRequest request,
            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
    ) {
        log.info("User create requested. username={}, email={}", request.username(), request.email());

        if (userRepository.existsByUsername(request.username())) {
            log.warn("User create failed. reason=duplicate_username, username={}", request.username());
            throw new UserAlreadyExistsException("username", request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            log.warn("User create failed. reason=duplicate_email, email={}", request.email());
            throw new UserAlreadyExistsException("email", request.email());
        }

        BinaryContent profile = optionalProfileCreateRequest
                .map(binaryContentService::createBinaryContent)
                .orElse(null);

        User user = userMapper.toEntity(request, profile);
        User savedUser = userRepository.save(user);

        UserStatus userStatus = userStatusMapper.toEntity(savedUser, Instant.now());
        UserStatus savedUserStatus = userStatusRepository.save(userStatus);

        log.info("User created. userId={}, username={}", savedUser.getId(), savedUser.getUsername());

        return userMapper.toResponse(savedUser, savedUserStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID userId) {
        User user = getUserOrThrow(userId);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse update(
            UUID userId,
            UserUpdateRequest request,
            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
    ) {
        log.info("User update requested. userId={}", userId);

        User user = getUserOrThrow(userId);

        if (request.newUsername() != null
                && !request.newUsername().equals(user.getUsername())
                && userRepository.existsByUsername(request.newUsername())) {
            log.warn("User update failed. reason=duplicate_username, userId={}, username={}", userId, request.newUsername());
            throw new UserAlreadyExistsException("username", request.newUsername());
        }

        if (request.newEmail() != null
                && !request.newEmail().equals(user.getEmail())
                && userRepository.existsByEmail(request.newEmail())) {
            log.warn("User update failed. reason=duplicate_email, userId={}, email={}", userId, request.newEmail());
            throw new UserAlreadyExistsException("email", request.newEmail());
        }

        BinaryContent oldProfile = user.getProfile();

        BinaryContent newProfile = optionalProfileCreateRequest
                .map(binaryContentService::createBinaryContent)
                .orElse(null);

        if (newProfile != null && oldProfile != null) {
            user.clearProfile();
        }

        user.update(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                newProfile
        );

        if (newProfile != null && oldProfile != null) {
            binaryContentService.delete(oldProfile.getId());
        }

        log.info("User updated. userId={}", user.getId());

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        log.warn("User delete requested. userId={}", userId);

        User user = getUserOrThrow(userId);
        UserStatus userStatus = getUserStatusByUserOrThrow(user);
        BinaryContent profile = user.getProfile();

        if (profile != null) {
            user.clearProfile();
        }

        userStatusRepository.delete(userStatus);
        userRepository.delete(user);

        if (profile != null) {
            binaryContentService.delete(profile.getId());
        }

        log.info("User deleted. userId={}", userId);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private UserStatus getUserStatusByUserOrThrow(User user) {
        return userStatusRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> UserStatusNotFoundException.byUserId(user.getId()));
    }
}