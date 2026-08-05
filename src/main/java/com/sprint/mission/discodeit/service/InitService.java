package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class InitService {

    @Value("${discodeit.admin.username}")
    private String adminUsername;
    @Value("${discodeit.admin.password}")
    private String adminPassword;
    @Value("${discodeit.admin.email}")
    private String adminEmail;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void initAdmin() {
        if (userRepository.existsByRole(Role.ADMIN)) {
            log.info("이미 ADMIN 권한을 가진 사용자가 존재합니다.");
            return;
        }


        String encodedPassword = passwordEncoder.encode(adminPassword);
        User admin = new User(adminUsername, adminEmail, encodedPassword, null, Role.ADMIN);

        userRepository.save(admin);

        log.info("관리자가 초기화되었습니다.");
    }
}
