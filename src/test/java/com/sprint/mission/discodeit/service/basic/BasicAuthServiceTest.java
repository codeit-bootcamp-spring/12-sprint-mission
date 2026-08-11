package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.user.Role;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;
    @Mock JwtTokenProvider jwtTokenProvider;
    @Mock JwtRegistry jwtRegistry;
    @InjectMocks BasicAuthService authService;

    @Test
    @DisplayName("사용자 권한 변경 성공")
    void updateRole_success() {
        UUID userId = UUID.randomUUID();
        UserRoleUpdateRequest request = new UserRoleUpdateRequest(userId, Role.CHANNEL_MANAGER);
        User user = User.builder().id(userId).username("user1").email("u@test.com")
                .password("encoded").role(Role.USER).build();
        UserResponse expected = new UserResponse(userId, "user1", "u@test.com", null, false,
                Role.CHANNEL_MANAGER);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMapper.toResponse(user, false)).willReturn(expected);

        UserResponse result = authService.updateRole(request);

        assertThat(result).isEqualTo(expected);
        assertThat(user.getRole()).isEqualTo(Role.CHANNEL_MANAGER);
        then(jwtRegistry).should().invalidateJwtInformationByUserId(userId);
    }

    @Test
    @DisplayName("사용자 권한 변경 실패 - 사용자 없음")
    void updateRole_fail_userNotFound() {
        UUID userId = UUID.randomUUID();
        UserRoleUpdateRequest request = new UserRoleUpdateRequest(userId, Role.ADMIN);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updateRole(request))
                .isInstanceOf(UserNotFoundException.class);
    }
}
