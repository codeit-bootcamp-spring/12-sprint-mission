package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private AuthService authService;

  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userDto = new UserDto(UUID.randomUUID(), "woody", "woody@email.com", null, true);
  }

  // ── login ────────────────────────────────────────────────

  @Test
  @DisplayName("로그인 성공")
  void login_Success() throws Exception {
    // given
    given(authService.login(any(LoginRequest.class))).willReturn(userDto);

    // when & then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new LoginRequest("woody", "password123"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("woody"));
  }

  @Test
  @DisplayName("존재하지 않는 사용자로 로그인 시 404 반환")
  void login_UserNotFound_Returns404() throws Exception {
    // given
    given(authService.login(any(LoginRequest.class)))
        .willThrow(new UserNotFoundException());

    // when & then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new LoginRequest("nobody", "password123"))))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("요청 바디 누락 시 400 반환")
  void login_MissingBody_Returns400() throws Exception {
    // when & then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                new LoginRequest("", ""))))
        .andExpect(status().isBadRequest());
  }
}
