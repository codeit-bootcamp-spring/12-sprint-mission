package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
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

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      try {
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        JwtDto jwtDto = new JwtDto(
            userDetails.getUserDto(),
            accessToken
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter()
            .write(objectMapper.writeValueAsString(jwtDto));
      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        ErrorResponse errorResponse = new ErrorResponse(
            e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );

        response.getWriter()
            .write(objectMapper.writeValueAsString(errorResponse));
      }
    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

      ErrorResponse errorResponse = new ErrorResponse(
          new RuntimeException("Authentication failed"),
          HttpServletResponse.SC_UNAUTHORIZED
      );

      response.getWriter()
          .write(objectMapper.writeValueAsString(errorResponse));
    }
  }
}
