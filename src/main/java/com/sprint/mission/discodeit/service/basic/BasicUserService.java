package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
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
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserDto create(UserCreateRequest request) {
        if (userRepository.findByUsername(request.userName()).isPresent()) {
            throw new IllegalStateException("사용중인 username 입니다.");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalStateException("사용중인 Email 입니다.");
        }
        User user = userRepository.create(request.toUser());
        return UserDto.from(user, userStatusRepository.create(new UserStatus(user.getId())));
    }

    @Override
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 User 입니다."));

        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 UserStatus 입니다."));
        return UserDto.from(user, userStatus);

    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 User 입니다."));

        UserStatus userStatus = userStatusRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 UserStatus 입니다."));
        return UserDto.from(user, userStatus);
    }

    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 User 입니다."));

        UserStatus userStatus = userStatusRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 UserStatus 입니다."));
        return UserDto.from(user, userStatus);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findById(user.getId())
                            .orElse(new UserStatus(user.getId()));
                    return UserDto.from(user, status);
                })
                .toList();
    }

    @Override
    public UserDto update(UUID id, UserUpdateRequest request) {
        User found = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 사용자입니다. id : " + id));
        if(!found.getUserName().equals(request.userName())) {
            if (userRepository.findByUsername(request.userName()).isPresent()) {
                throw new IllegalStateException("사용중인 username 입니다.");
            }
        }
        if(!found.getEmail().equals(request.email())){
            if (userRepository.findByEmail(request.email()).isPresent()) {
                throw new IllegalStateException("사용중인 Email 입니다.");
            }
        }
        User user = new User(request.userName(), request.password(),
                request.email(), request.nickName(),request.profileId());

        UserStatus status = userStatusRepository.findById(id)
                .orElse(new UserStatus(id));

        return UserDto.from(userRepository.update(id, user), status);
    }

    @Override
    public boolean delete(UUID id) {
        userStatusRepository.delete(id);
        return userRepository.delete(id);
    }
}
