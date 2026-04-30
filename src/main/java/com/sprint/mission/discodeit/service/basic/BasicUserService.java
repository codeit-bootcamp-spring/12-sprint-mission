package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
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
import java.util.Optional;
import java.util.UUID;

// 서비스 설계
// 1. DTO를 쓴다(인자와 리턴타입 둘다)
// 2. optional을 쓰지 않는다
// 3. non check 예외를 발생시킨다.
// -> 리포와 다른점

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;


    @Override
    public User create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        if(userRepository.existsByUsername(request.username())){
            throw new IllegalStateException("Username is already in use");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("email already exists");
        }

        // Profile
        UUID nullableProfileId = optionalProfileCreateRequest
                .map(profileRequest -> {
                    String filename = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(filename, contentType, bytes, (long)bytes.length);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                        .orElse(null);

        // User 생성
        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                nullableProfileId
        );

        // 저장
        User savedUser = userRepository.save(user);

        // UserStatus 생성
        Instant now = Instant.now();
        UserStatus status = new UserStatus(savedUser.getId(), now);
        userStatusRepository.save(status);

        return savedUser;
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public User update(UUID userId,
                       UserUpdateRequest userUpdateRequest,
                       Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        String newEmail = userUpdateRequest.newEmail() != null
                ? userUpdateRequest.newEmail()
                : user.getEmail();

        String newUsername = userUpdateRequest.newUsername() != null
                ? userUpdateRequest.newUsername()
                : user.getUsername();

        String newPassword = userUpdateRequest.newPassword() != null
                ? userUpdateRequest.newPassword()
                : user.getPassword();

        if (userUpdateRequest.newEmail() != null &&
                !newEmail.equals(user.getEmail())) {

            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalStateException("Email already in use");
            }
        }

        if (userUpdateRequest.newUsername() != null &&
                !newUsername.equals(user.getUsername())) {

            if (userRepository.existsByUsername(newUsername)) {
                throw new IllegalStateException("Username already in use");
            }
        }

        UUID nullableProfileId = optionalProfileCreateRequest
                .map(profileRequest -> {
                    String filename = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(filename, contentType, bytes, (long)bytes.length);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(user.getProfileId());

            user.update(newEmail, newUsername, newPassword, nullableProfileId);

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        userStatusRepository.findByUserId(userId)
                .ifPresent(status -> userStatusRepository.deleteById(status.getId()));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        userRepository.deleteById(userId);
    }


    private UserDto toDto(User user) {
        Boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(null);

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online
        );
    }
}
