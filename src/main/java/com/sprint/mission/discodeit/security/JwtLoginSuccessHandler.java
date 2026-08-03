package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException {
    DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();
    String accessToken = jwtTokenProvider.generateAccessToken(principal);
    String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

    Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(request.isSecure());
    refreshTokenCookie.setPath("/");
    response.addCookie(refreshTokenCookie);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(
        response.getOutputStream(),
        new JwtDto(principal.getUserDto(), accessToken)
    );
  }
}
