package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtTokenProviderTest {

  private final JwtTokenProvider provider =
      new JwtTokenProvider("test-environment-jwt-secret-key-32", 60, 120);

  @Test
  void issuesValidTokensAndRefreshesOnlyWithRefreshToken() {
    UserDetails user = User.withUsername("tester")
        .password("unused")
        .roles("USER")
        .build();

    String accessToken = provider.generateAccessToken(user);
    String refreshToken = provider.generateRefreshToken(user);

    assertThat(provider.validateToken(accessToken)).isTrue();
    assertThat(provider.validateToken(refreshToken)).isTrue();
    assertThat(provider.validateToken(provider.refreshAccessToken(refreshToken))).isTrue();
    assertThatThrownBy(() -> provider.refreshAccessToken(accessToken))
        .isInstanceOf(IllegalArgumentException.class);
    assertThat(provider.validateToken(accessToken + "tampered")).isFalse();
  }
}
