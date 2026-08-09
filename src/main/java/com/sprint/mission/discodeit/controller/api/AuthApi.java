package com.sprint.mission.discodeit.controller.api;


import com.sprint.mission.discodeit.dto.data.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {
    ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);
    ResponseEntity<UserDto> updateRole(UserRoleUpdateRequest userRoleUpdateRequest);
    ResponseEntity<JwtDto> refreshToken(String refreshToken,HttpServletResponse response);
}