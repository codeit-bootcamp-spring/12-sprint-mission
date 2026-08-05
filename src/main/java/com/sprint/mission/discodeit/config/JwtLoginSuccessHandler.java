package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.auth.JwtDto;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException {
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    String accessToken = jwtTokenProvider.createAccessToken(userDetails.getUserResponse());
    String refreshToken = jwtTokenProvider.createRefreshToken(userDetails.getUserResponse());

    jwtRegistry.registerJwtInformation(new JwtInformation(
        userDetails.getUserResponse(),
        accessToken,
        refreshToken,
        Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiration())
    ));

    ResponseCookie refreshTokenCookie = ResponseCookie.from(
            JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME,
            refreshToken)
        .httpOnly(true)
        .secure(request.isSecure())
        .path("/")
        .sameSite("Strict")
        .maxAge(Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpiration()))
        .build();

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    objectMapper.writeValue(response.getOutputStream(),
        new JwtDto(userDetails.getUserResponse(), accessToken));
  }
}
