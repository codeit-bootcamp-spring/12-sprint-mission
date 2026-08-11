package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.user.Role;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;
    @Mock BinaryContentService binaryContentService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtRegistry jwtRegistry;
    @InjectMocks BasicUserService userService;

    @Test
    @DisplayName("회원가입 시 비밀번호를 인코딩하고 USER 권한으로 저장")
    void create_success() {
        UserCreateRequest request = new UserCreateRequest("user1", "user1@test.com", "password");
        UUID userId = UUID.randomUUID();
        User user = User.builder().username("user1").email("user1@test.com")
                .password("encoded").role(Role.USER).build();
        User saved = User.builder().id(userId).username("user1").email("user1@test.com")
                .password("encoded").role(Role.USER).build();
        UserResponse expected = new UserResponse(userId, "user1", "user1@test.com", null, false,
                Role.USER);

        given(userRepository.existsByUsername("user1")).willReturn(false);
        given(userRepository.existsByEmail("user1@test.com")).willReturn(false);
        given(passwordEncoder.encode("password")).willReturn("encoded");
        given(userMapper.toEntity(request, null, "encoded", Role.USER)).willReturn(user);
        given(userRepository.save(user)).willReturn(saved);
        given(userMapper.toResponse(saved, false)).willReturn(expected);

        UserResponse result = userService.create(request, Optional.empty());

        assertThat(result).isEqualTo(expected);
        then(passwordEncoder).should().encode("password");
        then(userRepository).should().save(user);
    }

    @Test
    @DisplayName("회원가입 실패 - username 중복")
    void create_fail_duplicateUsername() {
        UserCreateRequest request = new UserCreateRequest("user1", "user1@test.com", "password");
        given(userRepository.existsByUsername("user1")).willReturn(true);

        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
                .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should().existsByUsername("user1");
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("비밀번호 수정 시 새 비밀번호를 인코딩")
    void update_encodesPassword() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("user1").email("old@test.com")
                .password("old-encoded").role(Role.USER).build();
        UserUpdateRequest request = new UserUpdateRequest(null, null, "new-password");
        UserResponse expected = new UserResponse(userId, "user1", "old@test.com", null, true,
                Role.USER);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.encode("new-password")).willReturn("new-encoded");
        given(jwtRegistry.hasActiveJwtInformationByUserId(userId)).willReturn(true);
        given(userMapper.toResponse(user, true)).willReturn(expected);

        UserResponse result = userService.update(userId, request, Optional.empty());

        assertThat(result).isEqualTo(expected);
        assertThat(user.getPassword()).isEqualTo("new-encoded");
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void delete_success() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("user1").email("u@test.com")
                .password("encoded").role(Role.USER).build();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        userService.delete(userId);

        then(userRepository).should().delete(user);
        then(binaryContentService).shouldHaveNoInteractions();
    }
}
