package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateDTO;
import com.sprint.mission.discodeit.dto.UserFindDTO;
import com.sprint.mission.discodeit.dto.UserUpdateDTO;
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
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateDTO userCreateDTO) {
        UUID profileId = null;
        if (userRepository.existByEmail(userCreateDTO.getEmail())) {
            throw new IllegalArgumentException("User with email " + userCreateDTO.getEmail() + " already exists");
        }
        if (userRepository.existByUsername(userCreateDTO.getUsername())) {
            throw new IllegalArgumentException("User with email " + userCreateDTO.getUsername() + " already exists");
        }
        if (userCreateDTO.getProfileImageType() != null) {
            BinaryContent binaryContent = new BinaryContent(
                    userCreateDTO.getProfileImageType(),
                    userCreateDTO.getProfileImage());
            binaryContentRepository.save(binaryContent);
            profileId = binaryContent.getId();
        }
        User user = new User(userCreateDTO.getUsername(),
                userCreateDTO.getEmail(),
                userCreateDTO.getPassword(),
                profileId);
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);
        return userRepository.save(user);
    }

    @Override
    public UserFindDTO find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userId + " not found"));
        return UserFindDTO.from(user, userStatus);
    }

    @Override
    public List<UserFindDTO> findAll() {
        return userRepository.findAll().stream()
                .map(elm -> {
                    UserStatus userStatus = userStatusRepository.findByUserId(elm.getId())
                            .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + elm.getId() + " not found"));
                    return UserFindDTO.from(elm, userStatus);
                }).toList();
    }

    @Override
    public User update(UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(userUpdateDTO.getId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + userUpdateDTO.getId() + " not found"));
        user.update(userUpdateDTO.getUsername(), userUpdateDTO.getEmail(), userUpdateDTO.getPassword());
        if (userUpdateDTO.getProfileImageType() != null) {
            binaryContentRepository.findById(user.getProfileId()).ifPresentOrElse(
                    binaryContent -> binaryContent.update(userUpdateDTO.getProfileImageType(), userUpdateDTO.getProfileImage()),
                    () -> binaryContentRepository.save(new BinaryContent(userUpdateDTO.getProfileImageType(), userUpdateDTO.getProfileImage()))
            );
        }
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        userStatusRepository.deleteByUserId(user.getId());
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userRepository.deleteById(userId);
    }
}
