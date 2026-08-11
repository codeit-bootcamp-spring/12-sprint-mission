package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.user.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.AuthException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public UserResponse updateRole(UserRoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        user.updateRole(request.newRole());

        jwtRegistry.invalidateJwtInformationByUserId(user.getId());

        return userMapper.toResponse(user, false);
    }

    @Override
    @Transactional(readOnly = true)
    public JwtInformation refresh(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)
                || !jwtTokenProvider.validateRefreshToken(refreshToken)
                || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        try {
            UUID userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthException(ErrorCode.INVALID_REFRESH_TOKEN));

            UserResponse userResponse = userMapper.toResponse(user, true);

            String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

            JwtInformation newJwtInformation = new JwtInformation(userResponse,newAccessToken,newRefreshToken);

            jwtRegistry.rotateJwtInformation(refreshToken, newJwtInformation);

            return newJwtInformation;

        } catch (IllegalArgumentException e) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
