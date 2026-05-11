package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("basicAuthService")
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    public User login(AuthRequest authRequest) {
        if (authRequest.username() == null || authRequest.username().isBlank() ||
                authRequest.password() == null || authRequest.password().isBlank()) {
            throw new IllegalArgumentException("Username and password must not be null (login)");
        }

        User user = userRepository.findByUsername(authRequest.username())
                .orElseThrow(() -> new IllegalArgumentException("Username not found : " + authRequest.username()));

        if (!user.getPassword().equals(authRequest.password())) {
            throw new IllegalArgumentException("Wrong password");
        }

        return user;
    }

}
