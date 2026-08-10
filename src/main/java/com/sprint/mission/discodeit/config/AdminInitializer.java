package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
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

  @Value("${discodeit.admin.password}")
  private String adminPassword;

  @Transactional
  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (userRepository.existsByRole(Role.ADMIN)) {
      log.info("관리자 계정이 이미 존재합니다.");
      return;
    }

    User admin = new User(
        "admin",
        "admin@discodeit.com",
        passwordEncoder.encode(adminPassword),
        null
    );

    admin.updateRole(Role.ADMIN);

    userRepository.save(admin);

    log.info("관리자 계정 초기화 완료: username={}", admin.getUsername());
  }
}
