package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User with username " + username + " not found"));

        if (!Objects.equals(user.getPassword(), password)) {
            throw new IllegalArgumentException("Wrong password");
        }
        return user;
    }
}
