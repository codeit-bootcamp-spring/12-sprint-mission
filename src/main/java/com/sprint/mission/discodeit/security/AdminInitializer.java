package com.sprint.mission.discodeit.security;

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

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements ApplicationRunner {

  @Value("${discodeit.admin.username}")
  private String adminUsername;
  @Value("${discodeit.admin.email}")
  private String adminMail;
  @Value("${discodeit.admin.password}")
  private String adminPassword;

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    if (userRepository.existsByRole(Role.ADMIN)) {
      log.warn("이미 관리자가 존재합니다.");
      return;
    }
    User admin = new User(adminUsername, adminMail, passwordEncoder.encode(adminPassword),
        null, Role.ADMIN);
    userRepository.save(admin);
  }
}
