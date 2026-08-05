package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.config.DiscodeitUserDetails;
import com.sprint.mission.discodeit.config.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.config.JwtTokenProvider;
import com.sprint.mission.discodeit.config.JwtInformation;
import com.sprint.mission.discodeit.config.JwtRegistry;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.TokenRefreshResult;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.jwt.JwtException;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import java.time.Instant;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final DiscodeitUserDetailsService userDetailsService;

  @Override
  public TokenRefreshResult refresh(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new JwtException(ErrorCode.JWT_INVALID_TOKEN);
    }
    if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      throw new JwtException(ErrorCode.JWT_INVALID_TOKEN);
    }

    JwtTokenProvider.TokenPair tokenPair = jwtTokenProvider.refreshTokens(refreshToken);
    try {
      DiscodeitUserDetails userDetails = (DiscodeitUserDetails) userDetailsService
          .loadUserByUsername(tokenPair.username());
      JwtDto jwtDto = new JwtDto(userDetails.getUserResponse(), tokenPair.accessToken());
      jwtRegistry.rotateJwtInformation(refreshToken, new JwtInformation(
          userDetails.getUserResponse(),
          tokenPair.accessToken(),
          tokenPair.refreshToken(),
          Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiration())
      ));

      return new TokenRefreshResult(
          jwtDto,
          tokenPair.refreshToken(),
          jwtTokenProvider.getRefreshTokenExpiration()
      );

    } catch (UsernameNotFoundException e) {
      throw new JwtException(ErrorCode.JWT_INVALID_TOKEN, e);
    }
  }
}
