package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authorization 헤더의 Bearer 엑세스 토큰으로 인증을 완료시키는 필터.
 *
 * <p>토큰이 없거나 유효하지 않으면 인증하지 않고 그대로 통과시킨다. 401 응답은 뒤쪽의
 * AuthorizationFilter와 RestAuthenticationEntryPoint가 판단해서 내려주므로, 이 필터가 직접
 * 응답을 만들면 permitAll 요청까지 막히게 된다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String accessToken = resolveAccessToken(request);

    if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      authenticate(request, accessToken);
    }

    filterChain.doFilter(request, response);
  }

  private void authenticate(HttpServletRequest request, String accessToken) {
    // 1) 서명, 만료, 용도 검사 (서버 상태와 무관한 토큰 자체의 유효성)
    if (!jwtTokenProvider.isValidAccessToken(accessToken)) {
      log.debug("유효하지 않은 엑세스 토큰: {} {}", request.getMethod(), request.getRequestURI());
      return;
    }
    // 2) 레지스트리 검사 (로그아웃, 권한 변경, 동시 로그인으로 무효화되지 않았는지)
    if (!jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken)) {
      log.debug("무효화된 엑세스 토큰: {} {}", request.getMethod(), request.getRequestURI());
      return;
    }

    jwtRegistry.findJwtInformationByAccessToken(accessToken).ifPresent(jwtInformation -> {
      // 레지스트리에 담긴 UserDto를 그대로 쓰면 요청마다 사용자를 다시 조회하지 않아도 된다.
      // 비밀번호는 인증이 끝난 뒤라 필요 없으므로 빈 문자열을 넣는다.
      DiscodeitUserDetails userDetails =
          new DiscodeitUserDetails(jwtInformation.getUserDto(), "");

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.debug("토큰 인증 성공: username={}", userDetails.getUsername());
    });
  }

  private String resolveAccessToken(HttpServletRequest request) {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith(BEARER_PREFIX)) {
      return null;
    }
    String token = header.substring(BEARER_PREFIX.length()).trim();
    return token.isEmpty() ? null : token;
  }
}
