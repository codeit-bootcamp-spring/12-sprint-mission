package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

  @Autowired UserService userService;

  @Test
  @DisplayName("사용자 생성 후 전체 조회 성공")
  void createAndFindAll() {
    UserCreateRequest req = new UserCreateRequest("integuser", "integ@email.com", "password123!");
    UserDto created = userService.create(req, Optional.empty());

    assertThat(created).isNotNull();
    assertThat(created.username()).isEqualTo("integuser");

    List<UserDto> all = userService.findAll();
    assertThat(all).anyMatch(u -> u.username().equals("integuser"));
  }

  @Test
  @DisplayName("이메일 중복 사용자 생성 시 예외 발생")
  void createDuplicateEmail_throws() {
    userService.create(new UserCreateRequest("user1", "dup@email.com", "password123!"),
        Optional.empty());

    assertThatThrownBy(() ->
        userService.create(new UserCreateRequest("user2", "dup@email.com", "password123!"),
            Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void updateUser_success() {
    UserDto created = userService.create(
        new UserCreateRequest("before", "before@email.com", "password123!"), Optional.empty());

    UserDto updated = userService.update(created.id(),
        new UserUpdateRequest("after", "after@email.com", "newpassword!"), Optional.empty());

    assertThat(updated.username()).isEqualTo("after");
  }

  @Test
  @DisplayName("사용자 삭제 후 조회 시 예외 발생")
  void deleteUser_thenNotFound() {
    UserDto created = userService.create(
        new UserCreateRequest("todelete", "todelete@email.com", "password123!"), Optional.empty());

    userService.delete(created.id());

    assertThatThrownBy(() -> userService.find(created.id()))
        .isInstanceOf(UserNotFoundException.class);
  }
}
