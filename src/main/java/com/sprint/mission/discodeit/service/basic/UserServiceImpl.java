package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Primary
@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserDto create(UserCreateRequest request) {
        boolean usernameDuplicated = userRepository.findAll().stream()
                .anyMatch(u -> u.getUsername().equals(request.getUsername()));
        if (usernameDuplicated) {
            throw new IllegalArgumentException("이미 사용 중인 username입니다: " + request.getUsername());
        }
        boolean emailDuplicated = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(request.getEmail()));
        if (emailDuplicated) {
            throw new IllegalArgumentException("이미 사용 중인 email입니다: " + request.getEmail());
        }
        User user = new User(request.getUsername(), request.getEmail(), request.getPassword());
        userRepository.save(user);

        if (request.getProfileImage() != null) {
            BinaryContentCreateRequest imageRequest = request.getProfileImage();
            BinaryContent profileImage = new BinaryContent(
                    UUID.randomUUID().toString(),
                    imageRequest.getBytes(),
                    imageRequest.getFileName(),
                    imageRequest.getContentType(),
                    Instant.now(),
                    user.getId().toString(),
                    null
            );
            binaryContentRepository.save(profileImage);
        }

        UserStatus userStatus = new UserStatus(
                UUID.randomUUID().toString(),
                user.getId().toString(),
                Instant.now()
        );
        userStatusRepository.save(userStatus);

        return toDto(user, userStatus);
    }

    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User를 찾을 수 없습니다: " + userId));
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다: " + userId));
        return toDto(user, userStatus);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다: " + user.getId()));
                    return toDto(user, userStatus);
                })
                .toList();
    }

    @Override
    public UserDto update(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User를 찾을 수 없습니다: " + userId));

        if (request.getNewUsername() != null) {
            boolean usernameDuplicated = userRepository.findAll().stream()
                    .filter(u -> !u.getId().equals(userId))
                    .anyMatch(u -> u.getUsername().equals(request.getNewUsername()));
            if (usernameDuplicated) {
                throw new IllegalArgumentException("이미 사용 중인 username입니다: " + request.getNewUsername());
            }
        }
        if (request.getNewEmail() != null) {
            boolean emailDuplicated = userRepository.findAll().stream()
                    .filter(u -> !u.getId().equals(userId))
                    .anyMatch(u -> u.getEmail().equals(request.getNewEmail()));
            if (emailDuplicated) {
                throw new IllegalArgumentException("이미 사용 중인 email입니다: " + request.getNewEmail());
            }
        }

        user.update(request.getNewUsername(), request.getNewEmail(), request.getNewPassword());
        userRepository.save(user);

        if (request.getProfileImage() != null) {
            binaryContentRepository.findAllById(List.of())
                    .forEach(bc -> binaryContentRepository.deleteById(UUID.fromString(bc.getId())));

            BinaryContentCreateRequest imageRequest = request.getProfileImage();
            BinaryContent newProfile = new BinaryContent(
                    UUID.randomUUID().toString(),
                    imageRequest.getBytes(),
                    imageRequest.getFileName(),
                    imageRequest.getContentType(),
                    Instant.now(),
                    userId.toString(),
                    null
            );
            binaryContentRepository.save(newProfile);
        }

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다: " + userId));

        return toDto(user, userStatus);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User를 찾을 수 없습니다: " + userId));

        binaryContentRepository.findAllById(List.of()).stream()
                .filter(bc -> userId.toString().equals(bc.getUserId()))
                .forEach(bc -> binaryContentRepository.deleteById(UUID.fromString(bc.getId())));

        userStatusRepository.findByUserId(userId)
                .ifPresent(status -> userStatusRepository.deleteById(UUID.fromString(status.getId())));

        userRepository.deleteById(userId);
    }

    @Override
    public User create(String woody, String mail, String woody1234) {
        return null;
    }

    private UserDto toDto(User user, UserStatus userStatus) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                userStatus.isOnline()
        );
    }
}

