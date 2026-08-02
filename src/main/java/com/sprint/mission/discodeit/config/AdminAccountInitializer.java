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

// 애플리케이션 실행 시 ADMIN 계정이 하나도 없으면 초기 어드민 계정을 생성한다
@Slf4j
@RequiredArgsConstructor
@Component
public class AdminAccountInitializer implements ApplicationRunner {

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
    if (userRepository.existsByRole(Role.ADMIN)) {
      log.debug("어드민 계정이 이미 존재하여 초기화를 건너뜁니다.");
      return;
    }
    User admin = new User(adminUsername, adminEmail, passwordEncoder.encode(adminPassword),
        null, Role.ADMIN);
    userRepository.save(admin);
    log.info("어드민 계정을 초기화했습니다: username={}", adminUsername);
  }
}
