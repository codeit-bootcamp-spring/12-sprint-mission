package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
@Primary
@Service
@RequiredArgsConstructor

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDto login(LoginRequest request) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(request.getUsername()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("username 또는 password가 올바르지 않습니다."));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new NoSuchElementException("username 또는 password가 올바르지 않습니다.");
        }

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다: " + user.getId()));

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                userStatus.isOnline()
        );
    }
}