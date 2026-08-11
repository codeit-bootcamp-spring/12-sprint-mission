package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.user.Role;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BinaryContentService binaryContentService;
    private final PasswordEncoder passwordEncoder;
    private final JwtRegistry jwtRegistry;

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

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = userMapper.toEntity(request, profile, encodedPassword, Role.USER);
        User savedUser = userRepository.save(user);

        log.info("User created. userId={}, username={}", savedUser.getId(), savedUser.getUsername());

        return userMapper.toResponse(savedUser, false);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID userId) {
        User user = getUserOrThrow(userId);

        return userMapper.toResponse(user, jwtRegistry.hasActiveJwtInformationByUserId(user.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> userMapper.toResponse(user,
                        jwtRegistry.hasActiveJwtInformationByUserId(user.getId()))
                )
                .toList();
    }

    @PreAuthorize("#userId == authentication.principal.userResponse.id")
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

        String encodedPassword = request.newPassword() == null
                ? null
                : passwordEncoder.encode(request.newPassword());

        user.update(
                request.newUsername(),
                request.newEmail(),
                encodedPassword,
                newProfile
        );

        if (newProfile != null && oldProfile != null) {
            binaryContentService.delete(oldProfile.getId());
        }

        log.info("User updated. userId={}", user.getId());

        return userMapper.toResponse(user, jwtRegistry.hasActiveJwtInformationByUserId(user.getId()));
    }

    @PreAuthorize("#userId == authentication.principal.userResponse.id")
    @Override
    @Transactional
    public void delete(UUID userId) {
        log.info("User delete requested. userId={}", userId);

        User user = getUserOrThrow(userId);
        BinaryContent profile = user.getProfile();

        if (profile != null) {
            user.clearProfile();
        }

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
}