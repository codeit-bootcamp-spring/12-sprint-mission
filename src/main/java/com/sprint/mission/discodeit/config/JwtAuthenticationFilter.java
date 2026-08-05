package com.sprint.mission.discodeit.config;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.exception.jwt.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String authorization = request.getHeader(AUTHORIZATION_HEADER);

    if (authorization != null && authorization.startsWith(BEARER_PREFIX)
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      String accessToken = authorization.substring(BEARER_PREFIX.length());
      authenticate(accessToken, request);
    }

    filterChain.doFilter(request, response);
  }

  private void authenticate(String accessToken, HttpServletRequest request) {
    try {
      if (!jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken)) {
        return;
      }
      
      JWTClaimsSet claims = jwtTokenProvider.getAccessTokenClaims(accessToken);
      String username = claims.getStringClaim("username");
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (JwtException | ParseException | AuthenticationException e) {
      SecurityContextHolder.clearContext();
    }
  }
}
