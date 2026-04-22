package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.data.request.message.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.data.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.data.dto.UserDto;
import com.sprint.mission.discodeit.data.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.message.BinaryContent;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.entity.user.UserStatus;
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
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateRequest request) {

        String username = request.username();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 유저.");
        }

        String email = request.email();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일.");
        }

        BinaryContentCreateRequest bccr = request.binaryContentCreateRequest().orElse(null);

        UUID profileId = null;
        if (bccr != null) {
            profileId = binaryContentRepository.save(new BinaryContent(bccr.fileName(), bccr.fileData())).getId();
        }

        User user = new User(
                request.username(),
                request.password(),
                request.email(),
                profileId
        );

        userStatusRepository.save(new UserStatus(user.getId()));

        return userRepository.save(user);
    }

    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("유저 없음");
        }

        return toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public User update(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("유저 없음");
        }

        String newUsername = request.username();
        if (!user.getUsername().equals(newUsername)) {
            if (userRepository.findByUsername(newUsername).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 유저.");
            }
        }

        String newEmail = request.email();
        if (!user.getEmail().equals(newEmail)) {
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 이메일.");
            }
        }

        BinaryContentCreateRequest bccr = request.binaryContentCreateRequest().orElse(null);

        UUID profileId = user.getProfileId();
        if (bccr != null) {
            if (profileId != null) {
                binaryContentRepository.deleteById(profileId);
            }

            profileId = binaryContentRepository.save(new BinaryContent(bccr.fileName(), bccr.fileData())).getId();
        }

        user.update(newUsername, request.password(), newEmail, profileId);

        return userRepository.save(user);
    }

    @Override
    public User delete(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("유저 없음");
        }

        UUID profileId = user.getProfileId();
        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        userStatusRepository.deleteByUserId(userId);

        return userRepository.deleteById(userId);
    }

    public UserDto toDto(User user){
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);

        if (userStatus == null) {
            throw new IllegalArgumentException("유저 상태가 없을 수가 없는데?");
        }

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                userStatus.isOnline());
    }
}