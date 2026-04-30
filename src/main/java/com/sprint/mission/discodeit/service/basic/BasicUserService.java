package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.data.user.UserResponse;
import com.sprint.mission.discodeit.dto.data.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        boolean isDuplicate = userRepository.findAll().stream()
                .anyMatch(u -> u.getUsername().equals(request.username()) || u.getEmail().equals(request.email()));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 사용 중인 username 또는 email 입니다.");
        }

        User user = new User(request.username(), request.email(), request.password());

        if (request.profileImageBytes() != null) {
            BinaryContent profileImage = new BinaryContent(
                    request.profileImageBytes(),
                    request.profileImageName(),
                    request.profileImageContentType()
            );
            binaryContentRepository.save(profileImage);
            user.updateProfileId(profileImage.getId());
        }

        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        return toResponse(user, userStatus);
    }

    @Override
    public UserResponse find(UUID id) {
        User user = findUserByIdOrThrow(id);

        UserStatus userStatus = userStatusRepository.findById(user.getId()).orElse(null);

        return toResponse(user, userStatus);
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        Map<UUID, UserStatus> statusMap = userStatusRepository.findAll().stream()
                .collect(Collectors.toMap(UserStatus::getUserId, us -> us));
        return users.stream()
                .map(user -> toResponse(user, statusMap.get(user.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = findUserByIdOrThrow(id);

        boolean isDuplicate = userRepository.findAll().stream()
                .anyMatch(u -> !u.getId().equals(id) &&
                        (u.getUsername().equals(request.username()) || u.getEmail().equals(request.email())));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 사용 중인 username 또는 email 입니다.");
        }

        user.update(request.username(), request.email(), request.password());

        if (request.profileImageBytes() != null) {
            if (user.getProfileId() != null) {
                binaryContentRepository.deleteById(user.getProfileId());
            }
            BinaryContent newImage = new BinaryContent(
                    request.profileImageBytes(),
                    request.profileImageName(),
                    request.profileImageContentType()
            );
            binaryContentRepository.save(newImage);
            user.updateProfileId(newImage.getId());
        }

        userRepository.save(user);

        UserStatus userStatus = userStatusRepository.findAll().stream()
                .filter(us -> us.getUserId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        return toResponse(user, userStatus);
    }

    @Override
    public void delete(UUID id) {
        User user = findUserByIdOrThrow(id);

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        userStatusRepository.findAll().stream()
                .filter(us -> us.getUserId().equals(user.getId()))
                .findFirst()
                .ifPresent(us -> userStatusRepository.deleteById(us.getId()));

        userRepository.deleteById(id);
    }

    private User findUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserResponse toResponse(User user, UserStatus userStatus) {
        return UserResponse.builder()
                .id(user.getId())
                .profileId(user.getProfileId())
                .username(user.getUsername())
                .email(user.getEmail())
                .isOnline(userStatus != null && userStatus.isOnline())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}