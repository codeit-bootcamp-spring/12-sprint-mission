package com.sprint.mission.discodeit.service.basic;

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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {

        for (User u : userRepository.findAll()) {
            if (u.getUsername().equals(request.getUsername())) {
                throw new RuntimeException("username 중복");
            }
            if (u.getEmail().equals(request.getEmail())) {
                throw new RuntimeException("email 중복");
            }
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getNickname()
        );

        // 프로필 이미지 선택적으로 저장
        if (request.getFileName() != null && request.getContentType() != null && request.getData() != null) {
            BinaryContent profile = new BinaryContent(
                    request.getFileName(),
                    request.getContentType(),
                    request.getData(),
                    user.getId(),
                    null
            );

            binaryContentRepository.save(profile);
            user.updateProfile(profile.getId());
        }

        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .online(userStatus.isOnline())
                .build();
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id);
        UserStatus userStatus = userStatusRepository.findByUserId(id);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .online(userStatus != null && userStatus.isOnline())
                .build();
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId());

                    return UserResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .nickname(user.getNickname())
                            .online(userStatus != null && userStatus.isOnline())
                            .build();
                })
                .toList();
    }

    @Override
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
        }

        user.update(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getNickname()
        );

        // 프로필 이미지 교체
        if (request.getFileName() != null && request.getContentType() != null && request.getData() != null) {

            if (user.getProfileId() != null) {
                binaryContentRepository.delete(user.getProfileId());
            }

            BinaryContent newProfile = new BinaryContent(
                    request.getFileName(),
                    request.getContentType(),
                    request.getData(),
                    user.getId(),
                    null
            );

            binaryContentRepository.save(newProfile);
            user.updateProfile(newProfile.getId());
        }

        userRepository.save(user);

        UserStatus userStatus = userStatusRepository.findByUserId(id);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .online(userStatus != null && userStatus.isOnline())
                .build();
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
        }

        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        UserStatus userStatus = userStatusRepository.findByUserId(id);
        if (userStatus != null) {
            userStatusRepository.delete(userStatus.getId());
        }

        userRepository.delete(id);
    }
}