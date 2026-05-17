package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final UserStatusService userStatusService;
    private final UserMapper userMapper;

    @PostMapping(path = "login")
    @Override
    public ResponseEntity<UserDto> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest);
        UserDto response = userMapper.toDto(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    private Boolean resolveOnline(User user) {
        return userStatusService.findAll().stream()
                .filter(userStatus -> userStatus.getUser().getId().equals(user.getId()))
                .findFirst()
                .map(UserStatus::isOnline)
                .orElse(false);
    }
}
