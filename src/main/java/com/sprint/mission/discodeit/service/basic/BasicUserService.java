package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    if (userRepository.existsByEmail(userCreateRequest.email())) {
      throw new UserAlreadyExistsException("email", userCreateRequest.email());
    }
    if (userRepository.existsByUsername(userCreateRequest.username())) {
      throw new UserAlreadyExistsException("username", userCreateRequest.username());
    }

    // 프로필 이미지: DB에 메타만 저장하고, 파일 저장은 커밋 이후 리스너에게 위임
    BinaryContent profile = optionalProfileCreateRequest
        .map(req -> {
          BinaryContent bc = binaryContentRepository.save(
              new BinaryContent(req.fileName(), (long) req.bytes().length, req.contentType()));
          eventPublisher.publishEvent(new BinaryContentCreatedEvent(bc.getId(), req.bytes()));
          return bc;
        })
        .orElse(null);

    // 비밀번호는 해시로만 저장
    User user = new User(userCreateRequest.username(), userCreateRequest.email(),
        passwordEncoder.encode(userCreateRequest.password()), profile);
    User savedUser = userRepository.save(user);

    log.info("User created: id={}, username={}", savedUser.getId(), savedUser.getUsername());
    return userMapper.toDto(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll().stream().map(userMapper::toDto).toList();
  }

  // 수정은 비밀번호와 이메일까지 바꿀 수 있으므로 관리자에게도 열지 않고 본인으로 한정한다
  @Override
  @PreAuthorize("#userId == principal.userId")
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    if (!user.getEmail().equals(userUpdateRequest.newEmail())
        && userRepository.existsByEmail(userUpdateRequest.newEmail())) {
      throw new UserAlreadyExistsException("email", userUpdateRequest.newEmail());
    }
    if (!user.getUsername().equals(userUpdateRequest.newUsername())
        && userRepository.existsByUsername(userUpdateRequest.newUsername())) {
      throw new UserAlreadyExistsException("username", userUpdateRequest.newUsername());
    }

    BinaryContent newProfile = optionalProfileCreateRequest
        .map(req -> {
          // 기존 프로필 삭제 후 새 프로필 저장
          Optional.ofNullable(user.getProfile()).ifPresent(binaryContentRepository::delete);
          BinaryContent bc = binaryContentRepository.save(
              new BinaryContent(req.fileName(), (long) req.bytes().length, req.contentType()));
          eventPublisher.publishEvent(new BinaryContentCreatedEvent(bc.getId(), req.bytes()));
          return bc;
        })
        .orElse(null);

    // 비밀번호를 변경하는 경우에도 해시로 저장
    String newPassword = userUpdateRequest.newPassword() == null
        ? null
        : passwordEncoder.encode(userUpdateRequest.newPassword());

    user.update(userUpdateRequest.newUsername(), userUpdateRequest.newEmail(),
        newPassword, newProfile);
    log.info("User updated: id={}", userId);
    return userMapper.toDto(userRepository.save(user));
  }

  // 삭제는 어뷰징 계정 정리 같은 운영 동작이 필요하므로 RoleHierarchy 의도대로 ADMIN에게도 허용한다
  @Override
  @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    userRepository.delete(user);
    log.info("User deleted: id={}", userId);
  }
}
