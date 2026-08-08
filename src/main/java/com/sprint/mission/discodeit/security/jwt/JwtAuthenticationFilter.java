package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;
  private final JwtRegistry<UUID> jwtRegistry;

  // 필터에서 제외할 request를 탐지할 메서드
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getServletPath();
    // /api/auth/refresh이 중요!!
    // 쿠키를 기반으로 refresh을 검증함으로 access 토큰이 무효할때도 인증하기 위해서
    return path.equals("/api/auth/refresh") || path.equals("/api/auth/login");
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = resolveToken(request);
      if (StringUtils.hasText(token)) {
        if (tokenProvider.validateAccessToken(token)
            && jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
          UserDto userDto = tokenProvider.parseAccessToken(token).userDto();
          DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, null);
          UsernamePasswordAuthenticationToken authentication
              = new UsernamePasswordAuthenticationToken(userDetails, null,
              userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.debug("인증 정보 설정 완료: username={}", userDto.username());
        } else {
          log.debug("유효하지 않은 JWT 토큰");
          sendErrorResponse(response, "유효하지 않은 JWT 토큰입니다.");
          return;
        }
      }
    } catch (Exception e) {
      log.error("인증 실패: {}", e.getMessage());
      SecurityContextHolder.clearContext();
      sendErrorResponse(response, "인증에 실패했습니다.");
      return;
    }
    filterChain.doFilter(request, response);
  }

  // Authorization: Bearer xxx 추출
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  // 공통 에러 응답
  private void sendErrorResponse(HttpServletResponse response, String message)
      throws IOException {
    log.debug(message);
    ErrorResponse errorResponse = new ErrorResponse(
        new DiscodeitException(ErrorCode.AUTHENTICATION_FAILED),
        HttpServletResponse.SC_UNAUTHORIZED);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
