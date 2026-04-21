package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthLoginDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User login(AuthLoginDTO authLoginDTO) {
        User user = userRepository.findByUsername(authLoginDTO.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User with username " + authLoginDTO.getUsername() + " not found"));
        if (user.getPassword().equals(authLoginDTO.getPassword())) {
            return user;
        } else {
            throw new IllegalArgumentException("User with password not matches");
        }
    }
}
