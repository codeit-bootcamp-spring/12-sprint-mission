package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.data.request.LoginRequest;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username()).orElse(null);
        
        if (user == null) {
            throw new IllegalArgumentException("유저 없음");
        }

        if (!request.password().equals(user.getPassword())) {
            throw new IllegalArgumentException("비밀번호 틀림");
        }

        return user;
    }
}
