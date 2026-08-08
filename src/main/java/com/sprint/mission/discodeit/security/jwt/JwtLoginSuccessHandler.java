package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry<UUID> jwtRegistry;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      try {
        // 토큰 발급 코드
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // refreshToken는 쿠키에 담고, secure, httpsonly 옵션 활성화해서 보내기!
        Cookie cookie = jwtTokenProvider.genereateRefreshTokenCookie(refreshToken);
        response.addCookie(cookie);

        // accessToken은 브라우저에서 저장할수 있도록 일반 응답(json)으로 보낼 예정
        JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), accessToken);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

        // JWT 세션에 발급된 refresh 토큰 저장
        jwtRegistry.registerJwtInformation(
            new JwtInformation(userDetails.getUserDto(), accessToken, refreshToken)
        );
        log.info("JWT 등록 성공: user={}", userDetails.getUserDto());
      } catch (Exception e) {
        log.error("JWT 발급 실패", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ErrorResponse errorResponse = new ErrorResponse(
            new DiscodeitException(ErrorCode.INTERNAL_SERVER_ERROR),
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
      }
    } else {
      log.error("인증 성공 콜백인데 principal 타입이 DiscodeitUserDetails가 아님");
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      ErrorResponse errorResponse = new ErrorResponse(
          new DiscodeitException(ErrorCode.AUTHENTICATION_FAILED),
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
  }
}
