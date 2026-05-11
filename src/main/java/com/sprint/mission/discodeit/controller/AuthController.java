package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.AuthRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @RequestMapping(value = "login", method = RequestMethod.POST)
  @ResponseBody
  public User login(@ModelAttribute AuthRequest authRequest) {
    User user = null;
    try {
      user = authService.login(authRequest);
    } catch (Exception e) {
      throw new RuntimeException("로그인 실패: " + e.getMessage());
    }
    return user;
  }
}
