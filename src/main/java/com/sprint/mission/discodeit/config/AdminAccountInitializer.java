package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminAccountInitializer {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Bean
  ApplicationRunner initializeAdminAccount(
      @Value("${discodeit.admin.email:admin@discodeit.com}") String email,
      @Value("${discodeit.admin.username:admin}") String username,
      @Value("${discodeit.admin.password:admin1234}") String password
  ) {
    return args -> {
      if (userRepository.existsByRole(UserRole.ADMIN)) {
        return;
      }

      User admin = new User(
          email,
          username,
          passwordEncoder.encode(password),
          UserRole.ADMIN,
          null
      );
      admin.initStatus();
      userRepository.save(admin);
    };
  }
}
