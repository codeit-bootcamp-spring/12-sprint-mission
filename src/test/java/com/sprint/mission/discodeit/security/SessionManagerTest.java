package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;

class SessionManagerTest {

  @Test
  void detects_online_user_from_session_registry() {
    SessionRegistryImpl sessionRegistry = new SessionRegistryImpl();
    SessionManager sessionManager = new SessionManager(sessionRegistry);
    UUID userId = UUID.randomUUID();
    DiscodeitUserDetails principal = userDetails(userId);

    sessionRegistry.registerNewSession("session-1", principal);

    assertThat(sessionManager.isOnline(userId)).isTrue();
    assertThat(sessionManager.getActiveSessionsByUserId(userId)).hasSize(1);
  }

  @Test
  void invalidates_user_sessions() {
    SessionRegistryImpl sessionRegistry = new SessionRegistryImpl();
    SessionManager sessionManager = new SessionManager(sessionRegistry);
    UUID userId = UUID.randomUUID();
    DiscodeitUserDetails principal = userDetails(userId);
    sessionRegistry.registerNewSession("session-1", principal);
    sessionRegistry.registerNewSession("session-2", principal);

    sessionManager.invalidateSessionsByUserId(userId);

    List<SessionInformation> activeSessions = sessionManager.getActiveSessionsByUserId(userId);
    assertThat(activeSessions).isEmpty();
    assertThat(sessionManager.isOnline(userId)).isFalse();
  }

  @Test
  void user_details_identity_is_based_on_user_id() {
    UUID userId = UUID.randomUUID();

    assertThat(userDetails(userId)).isEqualTo(userDetails(userId));
    assertThat(userDetails(userId).hashCode()).isEqualTo(userDetails(userId).hashCode());
  }

  private DiscodeitUserDetails userDetails(UUID userId) {
    UserDto userDto = new UserDto(
        userId,
        "user",
        "user@test.com",
        null,
        false,
        Role.USER
    );
    return new DiscodeitUserDetails(userDto, "password");
  }
}
