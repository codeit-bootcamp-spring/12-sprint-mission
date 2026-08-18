package com.sprint.mission.discodeit.exception;

import static com.sprint.mission.discodeit.support.SecurityTestSupport.asUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoResourceFoundHandlingTest {

  @Autowired MockMvc mockMvc;

  @Test
  @DisplayName("존재하지 않는 정적 리소스는 500이 아니라 404를 반환한다")
  void missingStaticResource_returns404() throws Exception {
    mockMvc.perform(get("/assets/does-not-exist.js"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
  }

  @Test
  @DisplayName("인증된 요청이 매핑되지 않은 경로로 오면 404를 반환한다")
  void unmappedPath_authenticated_returns404() throws Exception {
    mockMvc.perform(get("/definitely/not/a/route")
            .with(asUser(UUID.randomUUID(), "someone", Role.USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("인증되지 않은 요청은 경로 존재 여부를 알리지 않고 401을 반환한다")
  void unmappedPath_anonymous_returns401() throws Exception {
    // Security 필터가 MVC보다 앞이라 존재하지 않는 경로도 401이 된다.
    // 미인증 사용자에게 라우팅 구조를 노출하지 않는 편이 안전하므로 그대로 둔다.
    mockMvc.perform(get("/definitely/not/a/route"))
        .andExpect(status().isUnauthorized());
  }
}
