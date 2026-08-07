package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry<UUID> jwtRegistry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
            try {
                // 토큰 발급
                String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
                String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

                // refreshToken을 쿠키에 담기
                Cookie cookie = jwtTokenProvider.generateRefreshTokenCookie(refreshToken);
                response.addCookie(cookie);

                // accessToken을 브라우저에 저장.
                JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), accessToken);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(jwtDto));

                // JWT 세션에 발급된 refresh 토큰 저장
                jwtRegistry.registerJwtInformation( // registry에 토큰 저장하여 화이트 리스트로 활용
                        new JwtInformation(userDetails.getUserDto(), accessToken, refreshToken)
                );

                log.info("Successfully registered JWT for user {}", userDetails.getUserDto());

            } catch (Exception e) {
                log.error(e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                ErrorResponse errorResponse = new ErrorResponse(
                        "" + HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }
        } else {
            log.error("UNAUTHORIZED Error!!");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse errorResponse = new ErrorResponse(
                    "" + HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
