package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.user.Role;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${discodeit.admin.username}")
    private String adminUsername;

    @Value("${discodeit.admin.email}")
    private String adminEmail;

    @Value("${discodeit.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if(userRepository.existsByRole(Role.ADMIN)) {
            log.info("Admin initialization skipped. reason=admin_already_exists");
            return;
        }

        User admin = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        User savedAdmin = userRepository.save(admin);

        log.info(
                "Admin initialized. userId={}, username={}",
                savedAdmin.getId(),
                savedAdmin.getUsername()
        );
    }




}
