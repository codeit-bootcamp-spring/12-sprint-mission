package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.BinaryContent.createProfileImage;

@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    public BasicUserService(
            @Qualifier("jCFUserRepository") UserRepository userRepository,
            @Qualifier("jCFUserStatusRepository") UserStatusRepository userStatusRepository,
            @Qualifier("jCFBinaryContentRepository") BinaryContentRepository binaryContentRepository
    ) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public UserResponseDto create(UserCreateRequestDto dto) {

        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("이미 존재하는 유저네임입니다.");
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User(dto.username(), dto.email(), dto.password());
        userRepository.save(user);

        UUID profileImageId = null;

        if (dto.profileImage() != null){
            BinaryContent profileImage = createProfileImage(user.getId(), dto.profileImage());
            profileImageId = profileImage.getId();
             binaryContentRepository.save(profileImage);
        }

        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        return UserResponseDto.from(user, status, profileImageId);
    }

    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus userStatus = userStatusRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userId + " not found"));

        UUID profileImageId = binaryContentRepository.findByUserId(userId)
                .map(BinaryContent::getId)
                .orElse(null);

        return UserResponseDto.from(user, userStatus, profileImageId);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();

        List<UserResponseDto> userResponseDtos = new ArrayList<>();

        for (User user : users) {
            UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + user.getId() + " not found"));

            UUID profileImageId = binaryContentRepository.findByUserId(user.getId())
                    .map(BinaryContent::getId)
                    .orElse(null);

            userResponseDtos.add(UserResponseDto.from(user, userStatus, profileImageId));
        }
        return userResponseDtos;
    }

    @Override
    public UserResponseDto update(UserUpdateRequestDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + dto.userId() + " not found"));
        user.update(dto.name(), dto.email(), dto.password());

        User updateuser = userRepository.save(user);


        if (dto.profileImage() != null) {
            binaryContentRepository.deleteByUserId(dto.userId());

            BinaryContent newImage = createProfileImage(dto.userId(), dto.profileImage());
            binaryContentRepository.save(newImage);
        }

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + user.getId() + " not found"));

        UUID profileImageId = binaryContentRepository.findByUserId(dto.userId())
                .map(BinaryContent::getId)
                .orElse(null);

        return UserResponseDto.from(updateuser, userStatus, profileImageId);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        userStatusRepository.deleteByUserId(userId);
        binaryContentRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);

    }
}
