package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.Auth.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<UserResponse> login(@RequestBody AuthLoginRequest authLoginRequest) {
        UserResponse userResponse = authService.login(authLoginRequest);
        return ResponseEntity.ok(userResponse);
    }
}

// {
//        "id": "1038b2f2-6764-4427-af2b-d7661881a80e",
//                "username": "buzz",
//                "email": "buzz@test.com",
//                "createdAt": "2026-04-09T08:29:21.435032Z",
//                "updatedAt": "2026-04-09T08:29:21.435032Z",
//                "profileId": null,
//                "online": false
//                },