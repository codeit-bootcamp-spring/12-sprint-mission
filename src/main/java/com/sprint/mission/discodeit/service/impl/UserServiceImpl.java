package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
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

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;


    // 요구사항 1: 이름 및 이메일 중복방지 여부, 2. 프로필이미지 등록옵션 , 3. UserStatus 생성
    @Override
    public UserDto create(UserCreateRequest request) {
        userRepository.findByUsername(request.username())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 사용 중인 이름입니다.");
                });
        userRepository.findByEmail(request.email())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                });
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .nickname(request.nickname())
                .profileImageId(null)
                .build();
        User saveUser = userRepository.save(user);

        if (request.profileImage() != null) {
            BinaryContentCreateRequest profileImageRequest = request.profileImage();

            BinaryContent binaryContent = BinaryContent.builder()
                    .userId(saveUser.getId())
                    .messageId(null)
                    .binaryData(profileImageRequest.binaryData())
                    .build();

            BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

            saveUser.update(
                    saveUser.getUsername(),
                    saveUser.getEmail(),
                    saveUser.getPassword(),
                    saveUser.getNickname(),
                    savedBinaryContent.getId()
            );

            saveUser = userRepository.save(saveUser);
        }

        UserStatus userStatus = UserStatus.builder()
                .userId(saveUser.getId())
                .build();

        UserStatus saveUserStatus = userStatusRepository.save(userStatus);

        return UserDto.from(saveUser, saveUserStatus);
    }

    @Override
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);
        return UserDto.from(user, userStatus);

    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);
                    return UserDto.from(user, userStatus);
                })
                .toList();
    }

    @Override
    public UserDto update(UserUpdateRequest request) {
        User user = userRepository.findById(request.id()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        if (request.username() != null && !request.username().isEmpty()
                && !request.username().equals(user.getUsername())) {
            userRepository.findByUsername(request.username())
                    .ifPresent(foundUser -> {
                        throw new IllegalArgumentException("이미 사용 중인 username입니다.");
                    });

        }
        if (request.email() != null && !request.email().isBlank()
                && !request.email().equals(user.getEmail())) {
            userRepository.findByEmail(request.email())
                    .ifPresent(foundUser -> {
                        throw new IllegalArgumentException("이미 사용 중인 email입니다.");
                    });
        }
        String newUsername = request.username() != null && !request.username().isEmpty() ? request.username() : user.getUsername();
        String newEmail = request.email() != null && !request.email().isEmpty() ? request.email() : user.getEmail();
        String newPassword = request.password() != null && !request.password().isEmpty() ? request.password() : user.getPassword();
        String newNickname = request.nickname() != null && !request.nickname().isEmpty() ? request.nickname() : user.getNickname();

        UUID newProfileImageId = user.getProfileImageId();
        if (request.profileImage() != null) {
            if (newProfileImageId != null) {
                binaryContentRepository.delete(user.getProfileImageId());
            }
            BinaryContent profile = BinaryContent.builder()
                    .userId(user.getId())
                    .messageId(null)
                    .binaryData(request.profileImage().binaryData())
                    .build();

            profile = binaryContentRepository.save(profile);
            newProfileImageId = profile.getId();
        }
        user.update(newUsername, newEmail, newPassword, newNickname, newProfileImageId);
        userRepository.save(user);

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);

        return UserDto.from(user, userStatus);
    }

    @Override
    public UserDto delete(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);

        if (user.getProfileImageId() != null) {
            binaryContentRepository.delete(user.getProfileImageId());
        }
        if (userStatus != null) {
            userStatusRepository.delete(userStatus.getId());
        }

        userRepository.delete(id);
        return UserDto.from(user, userStatus);

    }
}

