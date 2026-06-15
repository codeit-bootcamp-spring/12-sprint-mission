package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private UserService userService;

  // CREATE

  @Test
  @DisplayName("[통합] 사용자 생성 성공")
  void create_Success() {
    // Given
    UserCreateRequest request = new UserCreateRequest(
        "testuser",
        "test@Codeit.com",
        "Password1234"
    );

    // When
    UserResponse response = userService.create(request, Optional.empty());

    // Then
    assertThat(response.id()).isNotNull();
    assertThat(response.username()).isEqualTo("testuser");
    assertThat(response.email()).isEqualTo("test@Codeit.com");
    assertThat(response.profile()).isNull();
    assertThat(response.online()).isTrue();
  }

  @Test
  @DisplayName("[통합] 사용자 생성 실패 - 이메일 중복")
  void create_Failure_DuplicateEmail() {
    // Given
    UserCreateRequest request = new UserCreateRequest(
        "testuser",
        "duplicate@Codeit.com",
        "Password1234"
    );
    userService.create(request, Optional.empty());

    UserCreateRequest duplicateRequest = new UserCreateRequest(
        "otheruser",
        "duplicate@Codeit.com",
        "Password1234"
    );

    // When & Then
    assertThatThrownBy(() -> userService.create(duplicateRequest, Optional.empty()))
        .isInstanceOf(RuntimeException.class);
  }

  // UPDATE

  @Test
  @DisplayName("[통합] 사용자 수정 성공")
  void update_Success() {
    // Given
    UserCreateRequest createRequest = new UserCreateRequest(
        "originaluser",
        "original@Codeit.com",
        "Password1234"
    );
    UserResponse created = userService.create(createRequest, Optional.empty());

    UserUpdateRequest updateRequest = new UserUpdateRequest(
        "updateduser",
        "updated@Codeit.com",
        "NewPassword1234"
    );

    // When
    UserResponse updated = userService.update(created.id(), updateRequest, Optional.empty());

    // Then
    assertThat(updated.id()).isEqualTo(created.id());
    assertThat(updated.username()).isEqualTo("updateduser");
    assertThat(updated.email()).isEqualTo("updated@Codeit.com");
  }

  @Test
  @DisplayName("[통합] 사용자 수정 실패 - 존재하지 않는 사용자")
  void update_Failure_UserNotFound() {
    // Given
    UUID nonExistentId = UUID.randomUUID();
    UserUpdateRequest updateRequest = new UserUpdateRequest(
        "updateduser",
        "updated@Codeit.com",
        "NewPassword1234"
    );

    // When & Then
    assertThatThrownBy(() -> userService.update(nonExistentId, updateRequest, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  // DELETE

  @Test
  @DisplayName("[통합] 사용자 삭제 성공")
  void delete_Success() {
    // Given
    UserCreateRequest createRequest = new UserCreateRequest(
        "deleteuser",
        "delete@Codeit.com",
        "Password1234"
    );
    UserResponse created = userService.create(createRequest, Optional.empty());

    // When
    userService.delete(created.id());

    // Then
    assertThatThrownBy(() -> userService.delete(created.id()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("[통합] 사용자 삭제 실패 - 존재하지 않는 사용자")
  void delete_Failure_UserNotFound() {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> userService.delete(nonExistentId))
        .isInstanceOf(UserNotFoundException.class);
  }

  // FIND ALL

  @Test
  @DisplayName("[통합] 사용자 목록 조회 성공")
  void findAll_Success() {
    // Given
    userService.create(new UserCreateRequest("user1", "user1@Codeit.com", "Password1234"),
        Optional.empty());
    userService.create(new UserCreateRequest("user2", "user2@Codeit.com", "Password1234"),
        Optional.empty());

    // When
    List<UserResponse> users = userService.findAll();

    // Then
    assertThat(users).hasSizeGreaterThanOrEqualTo(2);
    assertThat(users).extracting(UserResponse::username)
        .contains("user1", "user2");
  }

  @Test
  @DisplayName("[통합] 사용자 목록 조회 - 빈 목록")
  void findAll_Empty() {
    // When
    List<UserResponse> users = userService.findAll();

    // Then
    assertThat(users).isNotNull();
  }
}
