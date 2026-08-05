package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    if (!userRepository.existsByRole(Role.ADMIN)) {
      User admin = new User(
          "admin",
          "admin@admin.com",
          passwordEncoder.encode("Admin1234!"),
          null
      );

      admin.updateRole(Role.ADMIN);

      userRepository.save(admin);
    }
  }
}
