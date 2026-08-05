package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.TestSecuritySupport;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class UserApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  private UserDto userDto;

  @BeforeEach
  void setUp() throws Exception {
    userDto = userService.create(
        new UserCreateRequest("test1", "test1@test.com", "password1!"),
        Optional.empty()
    );
    TestSecuritySupport.authenticate(userDto.id(), Role.USER);
  }

  @AfterEach
  void tearDown() {
    TestSecuritySupport.clear();
  }

  @Test
  @DisplayName("create_user")
  public void createUser() throws Exception {
    UserCreateRequest userCreateRequest = new UserCreateRequest(
        "username",
        "email@email.com",
        "password123!"
    );

    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(userCreateRequest)
    );

    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "profile.png",
        "image/png",
        "profile".getBytes()
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequestPart)
            .file(profilePart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value(userCreateRequest.username()))
        .andExpect(jsonPath("$.email").value(userCreateRequest.email()))
        .andExpect(jsonPath("$.profile.fileName").value(profilePart.getOriginalFilename()))
        .andExpect(jsonPath("$.profile.size").value(profilePart.getSize()))
        .andExpect(jsonPath("$.profile.contentType").value(profilePart.getContentType()));
  }

  @Test
  @DisplayName("find_all")
  public void findAll() throws Exception {
    UserDto userDto1 = userService.create(
        new UserCreateRequest("test2", "test2@test.com", "password2!"),
        Optional.empty()
    );

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(3))
        .andExpect(jsonPath("$[*].id").value(hasItems(
            userDto.id().toString(),
            userDto1.id().toString()
        )))
        .andExpect(jsonPath("$[*].username").value(hasItems(
            userDto.username(),
            userDto1.username()
        )))
        .andExpect(jsonPath("$[*].email").value(hasItems(
            userDto.email(),
            userDto1.email()
        )));
  }

  @Test
  @DisplayName("update_user")
  public void updateUser() throws Exception {
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "updated_name",
        "updated_email@update.com",
        "updated_password1!"
    );

    MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(userUpdateRequest)
    );

    mockMvc.perform(multipart("/api/users/{userId}", userDto.id())
            .file(userUpdateRequestPart)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userDto.id().toString()))
        .andExpect(jsonPath("$.username").value(userUpdateRequest.newUsername()))
        .andExpect(jsonPath("$.email").value(userUpdateRequest.newEmail()));

  }

  @Test
  @DisplayName("update_user_forbidden_when_not_self")
  public void updateUser_forbidden_when_not_self() throws Exception {
    UserDto otherUserDto = userService.create(
        new UserCreateRequest("test2", "test2@test.com", "password2!"),
        Optional.empty()
    );
    TestSecuritySupport.authenticate(otherUserDto.id(), Role.USER);

    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "updated_name",
        "updated_email@update.com",
        "updated_password1!"
    );

    MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(userUpdateRequest)
    );

    mockMvc.perform(multipart("/api/users/{userId}", userDto.id())
            .file(userUpdateRequestPart)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("delete_user")
  public void deleteUser() throws Exception {
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2));

    mockMvc.perform(delete("/api/users/{userId}", userDto.id()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  @DisplayName("delete_user_forbidden_when_not_self")
  public void deleteUser_forbidden_when_not_self() throws Exception {
    UserDto otherUserDto = userService.create(
        new UserCreateRequest("test2", "test2@test.com", "password2!"),
        Optional.empty()
    );
    TestSecuritySupport.authenticate(otherUserDto.id(), Role.USER);

    mockMvc.perform(delete("/api/users/{userId}", userDto.id()))
        .andExpect(status().isForbidden());
  }

}
