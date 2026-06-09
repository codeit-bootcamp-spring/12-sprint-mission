package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// @EnableJpaAuditing이 별도 JpaAuditingConfig에 있으므로 @WebMvcTest에서 명시적으로 제외
// → JPA 인프라 없는 웹 슬라이스 테스트에서 "JPA metamodel must not be empty" 방지
@WebMvcTest(value = UserController.class,
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JpaAuditingConfig.class))
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockitoBean UserService userService;
  @MockitoBean UserStatusService userStatusService;

  @Test
  @DisplayName("GET /api/users - 사용자 목록 조회 성공")
  void findAll_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto dto = new UserDto(userId, "testuser", "test@email.com", null, true);
    given(userService.findAll()).willReturn(List.of(dto));

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("testuser"))
        .andExpect(jsonPath("$[0].email").value("test@email.com"));
  }

  @Test
  @DisplayName("POST /api/users - 사용자 생성 성공")
  void create_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto dto = new UserDto(userId, "newuser", "new@email.com", null, true);
    given(userService.create(any(UserCreateRequest.class), any(Optional.class))).willReturn(dto);

    UserCreateRequest req = new UserCreateRequest("newuser", "new@email.com", "password123!");
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(req));

    mockMvc.perform(multipart("/api/users")
            .file(requestPart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("newuser"));
  }

  @Test
  @DisplayName("DELETE /api/users/{userId} - 존재하지 않는 사용자 삭제 시 404")
  void delete_notFound_returns404() throws Exception {
    UUID userId = UUID.randomUUID();
    org.mockito.BDDMockito.willThrow(new UserNotFoundException(userId))
        .given(userService).delete(userId);

    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }
}
