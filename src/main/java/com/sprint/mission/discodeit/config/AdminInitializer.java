package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${discodeit.admin.username}")
  private String username;

  @Value("${discodeit.admin.email}")
  private String email;

  @Value("${discodeit.admin.password}")
  private String password;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (userRepository.existsByRole(Role.ADMIN)
        || userRepository.existsByUsername(username)
        || userRepository.existsByEmail(email)) {
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
