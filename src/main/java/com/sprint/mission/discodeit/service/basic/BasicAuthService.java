package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    @Value("${discodeit.admin.username") String username;
    @Value("${discodeit.admin.email") String email;
    @Value("${discodeit.admin.password") String password;

    @Transactional
    public UserDto updateRole(UserRoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);

        user.updateRole(request.newRole());

        return userMapper.toDto(user);
    }

    @Transactional
    public void initializeAdmin() {
        if(userRepository.existsByRole(Role.ADMIN)){
            return;
        }
        User admin = new User(
                username,
                email,
                passwordEncoder.encode(password),
                null,
                Role.ADMIN
        );
        userRepository.save(admin);
    }
}
