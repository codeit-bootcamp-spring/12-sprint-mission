package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.login.LoginRequestDto;
import com.sprint.mission.discodeit.dto.login.LoginResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    public BasicAuthService(
            @Qualifier("jCFUserRepository") UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(()->new NoSuchElementException("존재하지 않는 사용자입니다. username : " + dto.username()));

        if (!user.getPassword().equals(dto.password())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return new LoginResponseDto(user.getId(), user.getUsername(), user.getEmail());

    }
}
