package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserService userService;
    private final AuthService authService;

    @Value("${discodeit.admin.username}")
    private String adminUsername;
    @Value("${discodeit.admin.email}")
    private String adminEmail;
    @Value("${discodeit.admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        try {
            UserCreateRequest request = new UserCreateRequest(adminUsername, adminEmail, adminPassword);
            UserDto admin = userService.create(request, Optional.empty());
            authService.updateRole(new RoleUpdateRequest(admin.id(), Role.ADMIN));
            log.info("관리자가 초기화되었습니다.");
        } catch (UserAlreadyExistsException e) {
            log.warn("이미 관리자가 존재합니다.");
        } catch (Exception e) {
            log.error("관리자 생성 중 오류 발생: {}", e.getMessage());
        }
    }
}
