package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginUserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    public BasicAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(LoginUserRequest request) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(request.username()))
                .filter(u -> u.getPassword().equals(request.password()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("username or password wrong"));

        return user;
    }
}
