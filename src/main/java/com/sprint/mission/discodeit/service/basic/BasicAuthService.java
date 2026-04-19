package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest request) {
        User foundUser = userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(request.getUsername())
                        && user.getPassword().equals(request.getPassword()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("로그인 실패"));

        UserStatus userStatus = userStatusRepository.findByUserId(foundUser.getId());
        if (userStatus != null) {
            userStatus.updateLastSeen();
        }

        return UserResponse.builder()
                .id(foundUser.getId())
                .username(foundUser.getUsername())
                .email(foundUser.getEmail())
                .nickname(foundUser.getNickname())
                .online(userStatus != null && userStatus.isOnline())
                .build();
    }
}