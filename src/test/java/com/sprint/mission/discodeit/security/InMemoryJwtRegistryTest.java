package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryJwtRegistryTest {

  @Test
  void rejectsInvalidMaxActiveJwtCount() {
    assertThatThrownBy(() -> new InMemoryJwtRegistry(0))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void limitsConcurrentLoginAndRotatesTokens() {
    JwtRegistry registry = new InMemoryJwtRegistry(1);
    UserDto user = new UserDto(
        UUID.randomUUID(), "tester", "tester@example.com", null, true, Role.USER
    );
    registry.registerJwtInformation(new JwtInformation(user, "access-1", "refresh-1"));
    registry.registerJwtInformation(new JwtInformation(user, "access-2", "refresh-2"));

    assertThat(registry.hasActiveJwtInformationByAccessToken("access-1")).isFalse();
    assertThat(registry.hasActiveJwtInformationByAccessToken("access-2")).isTrue();

    registry.rotateJwtInformation(
        "refresh-2",
        new JwtInformation(user, "access-3", "refresh-3")
    );
    assertThat(registry.hasActiveJwtInformationByRefreshToken("refresh-2")).isFalse();
    assertThat(registry.hasActiveJwtInformationByRefreshToken("refresh-3")).isTrue();
  }
}
