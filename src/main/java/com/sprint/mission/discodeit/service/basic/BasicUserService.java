package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.exception.file.FileProcessingException;
import com.sprint.mission.discodeit.exception.user.UserDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.config.JwtRegistry;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PasswordEncoder passwordEncoder;
    private final JwtRegistry jwtRegistry;

    @Override
    public UserResponse create(UserCreateRequest request, MultipartFile profile) {
        validateDuplicateUser(request);

        BinaryContent binaryContent = null;
        if (hasProfile(profile)) {
            binaryContent = saveProfileImage(profile);
        }

        User user = new User(
                request.email(),
                request.username(),
                passwordEncoder.encode(request.password()),
                UserRole.USER,
                binaryContent
        );
        user.initStatus();
        User savedUser = userRepository.save(user);

        log.info("사용자 생성 완료. userId={}, username={}",
                savedUser.getId(),
                savedUser.getUsername()
        );
        return toUserResponse(savedUser);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse update(UUID userId, UserUpdateRequest request, MultipartFile profile) {
        User user = getUserOrThrow(userId);
        validateDuplicateForUpdate(userId, request);

        BinaryContent currentProfile = user.getProfile();
        UUID profileImageId = currentProfile == null ? null : currentProfile.getId();
        BinaryContent binaryContent = null;
        if (hasProfile(profile)) {
            if (profileImageId != null) {
                binaryContentRepository.deleteById(profileImageId);
            }

            binaryContent = saveProfileImage(profile);
        }

        user.changeProfile(
                request.newEmail(),
                request.newUsername(),
                binaryContent
        );
        userRepository.save(user);

        log.info("사용자 수정 완료. userId={}",
                user.getId()
        );
        return toUserResponse(user);
    }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse updateRole(UserRoleUpdateRequest request) {
        User user = getUserOrThrow(request.userId());
        user.changeRole(request.role());

        UserResponse response = toUserResponse(userRepository.save(user));
        jwtRegistry.invalidateJwtInformationByUserId(user.getId());
        return response;
    }

    @Override
    public void delete(UUID id) {
        User user = getUserOrThrow(id);

        BinaryContent profile = user.getProfile();
        if (profile != null && profile.getId() != null) {
            binaryContentRepository.deleteById(user.getProfile().getId());
        }

        userStatusRepository.findByUserId(user.getId())
                .ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));

        userRepository.deleteById(id);
        log.info("사용자 삭제 완료. userId={}", id);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
    }

    private UserResponse toUserResponse(User user) {
        UserResponse response = UserResponse.from(user);
        return new UserResponse(
                response.id(),
                response.username(),
                response.email(),
                response.profile(),
                jwtRegistry.hasActiveJwtInformationByUserId(user.getId()),
                response.role()
        );
    }

    private void validateDuplicateUser(UserCreateRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            log.warn("사용자 생성 실패 - 중복 사용자. username={}",
                    request.username()
            );

            throw UserDuplicateException.withUsername(request.username());
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("사용자 생성 실패 - 중복 사용자. email={}",
                    request.email()
            );

            throw UserDuplicateException.withEmail(request.email());
        }
    }

    private void validateDuplicateForUpdate(UUID userId, UserUpdateRequest request) {
        userRepository.findByEmail(request.newEmail())
                .filter(found -> !found.getId().equals(userId))
                .ifPresent(found -> {
                    log.warn("사용자 수정 실패 - 중복 사용자, newEmail={}",
                            request.newEmail()
                    );

                    throw UserDuplicateException.withEmail(request.newEmail());
                });

        userRepository.findByUsername(request.newUsername())
                .filter(found -> !found.getId().equals(userId))
                .ifPresent(found -> {
                    log.warn("사용자 수정 실패 - 중복 사용자, newUsername={}",
                            request.newUsername()
                    );

                    throw UserDuplicateException.withUsername(request.newUsername());
                });
    }

    private BinaryContent saveProfileImage(MultipartFile profile) {
        try {
            BinaryContent binaryContent = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getSize()
            );

            BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

            binaryContentStorage.put(
                    savedBinaryContent.getId(),
                    profile.getBytes()
            );

            return savedBinaryContent;
        } catch (IOException e) {
            log.error("프로필 이미지 저장 실패. fileName={}, contentType={}, size={}",
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getSize(),
                    e
            );

            throw new FileProcessingException(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getSize(),
                    e
            );
        }
    }

    private boolean hasProfile(MultipartFile profile) {
        return profile != null && !profile.isEmpty();
    }
}
