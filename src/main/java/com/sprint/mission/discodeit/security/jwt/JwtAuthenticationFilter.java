package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final JwtRegistry<UUID> jwtRegistry;

    @Override
    // http 요청이 들어왔을 때 controller 도달 전 내부 동작 메소드
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try{
            // 1. 헤더에서 토큰 추출
            String token = resolveToken(request);

            if(StringUtils.hasText(token)){
                // 2. token 유효성 검사 및 jwt 세션에서도 유효한지 확인.
                if(tokenProvider.validateAccessToken(token)
                    && jwtRegistry.hasActiveJwtInformationAccessToken(token)){
                    String username = tokenProvider.getUsernameFromToken(token);

                    // 3. 토큰 정보를 기반으로 인증정보 로딩
                    UserDto userDto = tokenProvider.parseAccessToken(token).userDto();
                    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, null);
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    // http 요청 부가 정보 받기 ( 요청을 보낸 클라이언트, 세션 id 등)
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // SecurityContext에 정보 등록
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.debug("Set authentication for user: {}", username);

                } else {
                    // 토큰 유효성 검사 실패시
                    log.debug("invalid JWT token");
                    sendErrorResponse(response,"invalid JWT Token", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Authentication failed. {}", e.getMessage());
            SecurityContextHolder.clearContext();
            sendErrorResponse(response,"Authentication failed.",HttpServletResponse.SC_UNAUTHORIZED);
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse("" + status, message);
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
