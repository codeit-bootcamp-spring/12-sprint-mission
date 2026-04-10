package com.sprint.mission.discodeit.service.Impl;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service("authService")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByNameAndPassword(request.name(),request.password())
                .orElseThrow(()-> new NoSuchElementException("로그인 실패"));

        return convertToResponse(user);
    }

    private LoginResponse convertToResponse(User user) {
        return new LoginResponse(
                user.getId(),
                Instant.now()
        );
    }
}
