package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.user.User;
import com.sprint.mission.discodeit.domain.user.UserStatus;
import com.sprint.mission.discodeit.dto.Auth.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(AuthLoginRequest dto) {
        Optional<User> user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(dto.username())
                        && u.getPassword().equals(dto.password()))
                .findFirst();

        if (user.isEmpty()) {
            throw new NoSuchElementException("Invalid username or password");
        }

        User foundUser = user.get();

        UserStatus userStatus = userStatusRepository.findByUserId(foundUser.getId())
                .orElseThrow(() -> new NoSuchElementException("User status not found"));

        return new UserResponse(
                foundUser.getId(),
                foundUser.getUsername(),
                foundUser.getEmail(),
                foundUser.getCreatedAt(),
                foundUser.getUpdatedAt(),
                foundUser.getProfileId(),
                userStatus.isOnline()
        );
    }
}