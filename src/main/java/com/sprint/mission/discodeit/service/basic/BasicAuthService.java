package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public UserDto updateRole(UserRoleUpdateRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() ->
                        UserNotFoundException.withId(request.userId()));

        user.updateRole(request.newRole());
        userRepository.save(user);

        sessionRegistry.getAllPrincipals().stream()
                .filter(principal ->
                        principal instanceof DiscodeitUserDetails userDetails
                                && userDetails.getUserDto().id()
                                .equals(request.userId())
                )
                .forEach(principal ->
                        sessionRegistry.getAllSessions(principal, false)
                                .forEach(SessionInformation::expireNow)
                );

        return userMapper.toDto(user);
    }
}