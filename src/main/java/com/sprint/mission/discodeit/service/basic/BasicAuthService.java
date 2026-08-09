package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;
    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry<UUID> jwtRegistry;
    private final UserDetailsService userDetailsService;

    @Value("${discodeit.admin.username}") String username;
    @Value("${discodeit.admin.email}") String email;
    @Value("${discodeit.admin.password}") String password;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateRole(UserRoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);
        UUID userId = user.getId();

        user.updateRole(request.newRole());

        sessionRegistry.getAllPrincipals().stream()
                .filter(DiscodeitUserDetails.class::isInstance)
                .map(DiscodeitUserDetails.class::cast)
                .filter(principal ->
                        principal.getUserDto().id().equals(userId)
                )
                .flatMap(principal ->
                        sessionRegistry
                                .getAllSessions(principal,false) // false : 만료된 세션 포함 X
                                .stream()
                )
                .forEach(SessionInformation::expireNow); // 전부 만료시킴
        return userMapper.toDto(user);
    }

    @Transactional
    public void initializeAdmin() {
        if(userRepository.existsByRole(Role.ADMIN)){
            return;
        }
        User admin = new User(
                username,
                email,
                passwordEncoder.encode(password),
                null,
                Role.ADMIN
        );
        userRepository.save(admin);
    }

    @Override
    public JwtInformation refreshToken(String refreshToken) {

        if(!tokenProvider.validateRefreshToken(refreshToken)
            ||!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {

            throw new RuntimeException("invalid or expired refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(userDetails == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        try{
            // 토큰 재발급
            DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails) userDetails;
            String newAccessToken = tokenProvider.generateAccessToken(discodeitUserDetails);
            String newRefreshToken = tokenProvider.generateRefreshToken(discodeitUserDetails);

            JwtInformation newJwtInformation = new JwtInformation(
                    discodeitUserDetails.getUserDto()
                    ,newAccessToken
                    ,newRefreshToken
            );

            jwtRegistry.rotateJwtInformation(refreshToken,newJwtInformation);
            return newJwtInformation;

        } catch (Exception e) {
            log.error("Failed to generate new tokens for user : {}", username, e);
            throw new RuntimeException("INTERNAL_SERVER_ERROR");
        }
    }
}
