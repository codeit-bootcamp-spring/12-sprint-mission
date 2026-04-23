package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // [ ] 사용자는 로그인할 수 있다.
    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ResponseEntity<User> login(@RequestBody LoginRequest login){
        User result = authService.login(login);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
