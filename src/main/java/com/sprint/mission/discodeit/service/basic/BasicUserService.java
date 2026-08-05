package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UsernameAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final PasswordEncoder passwordEncoder;
  private final SessionManager sessionManager;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest) {
    if (userCreateRequest == null) {
      throw new IllegalArgumentException("userCreateRequest is null.");
    }

    if (!isUniqueUsername(userCreateRequest.username())) {
      throw UsernameAlreadyExistsException.withUsername(userCreateRequest.username());
    }

    if (!isUniqueEmail(userCreateRequest.email())) {
      throw EmailAlreadyExistsException.withEmail(userCreateRequest.email());
    }

    BinaryContent binaryContent = createProfile(profileCreateRequest);

//    매퍼 변경을 하지 않고 dto 추가 생성 및 인코딩
    UserCreateRequest passwordEncodedRequest = new UserCreateRequest(
        userCreateRequest.username(),
        userCreateRequest.email(),
        passwordEncoder.encode(userCreateRequest.password())
    );

    User user = userMapper.toEntity(passwordEncodedRequest, binaryContent);

    User savedUser = userRepository.save(user);
    UserDto userDto = userMapper.toDto(savedUser);

    log.info("사용자 생성 완료: userId={}, username={}",
        userDto.id(),
        userDto.username()
    );

    return userDto;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findDetailById(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("id is null.");
    }

    User user = userRepository.findDetailById(userId)
        .orElseThrow(() -> UserNotFoundException.withUserId(userId));

    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAllWithFetch() {
    return userRepository.findAllWithFetch().stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  @PreAuthorize("#userId.equals(principal.userDto.id)")
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest) {
    if (userId == null) {
      throw new IllegalArgumentException("userId is null.");
    }
    if (userUpdateRequest == null) {
      throw new IllegalArgumentException("userUpdateRequest is null.");
    }

    User user = userRepository.findDetailById(userId)
        .orElseThrow(() -> UserNotFoundException.withUserId(userId));

    String beforeUsername = user.getUsername();
    String beforeEmail = user.getEmail();
    boolean profileChanged = profileCreateRequest.isPresent();

    if (userUpdateRequest.newUsername() != null) {
      if (!Objects.equals(userUpdateRequest.newUsername(), user.getUsername())
          && !isUniqueUsername(userUpdateRequest.newUsername())) {
        throw UsernameAlreadyExistsException.withUsername(userUpdateRequest.newUsername());
      }
      user.setUsername(userUpdateRequest.newUsername());
    }
    if (userUpdateRequest.newEmail() != null) {
      if (!Objects.equals(userUpdateRequest.newEmail(), user.getEmail())
          && !isUniqueEmail(userUpdateRequest.newEmail())) {
        throw EmailAlreadyExistsException.withEmail(userUpdateRequest.newEmail());
      }
      user.setEmail(userUpdateRequest.newEmail());
    }

    if (userUpdateRequest.newPassword() != null) {
      user.setPassword(passwordEncoder.encode(userUpdateRequest.newPassword()));
    }

    BinaryContent oldProfileImage = null;
    if (profileCreateRequest.isPresent()) {
      oldProfileImage = user.getProfile();

      BinaryContent newProfileImage = createProfile(profileCreateRequest);
      user.setProfile(newProfileImage);
    }

    User savedUser = userRepository.save(user);
    if (oldProfileImage != null) {
      UUID oldProfileImageId = oldProfileImage.getId();
      binaryContentRepository.delete(oldProfileImage);
      deleteBinaryContentAfterCommit(oldProfileImageId);
    }

    UserDto userDto = userMapper.toDto(savedUser);

    log.info(
        "사용자 수정 완료: userId={}, usernameChanged={}, emailChanged={}, passwordChanged={}, profileChanged={}",
        userId,
        !Objects.equals(beforeUsername, user.getUsername()),
        !Objects.equals(beforeEmail, user.getEmail()),
        userUpdateRequest.newPassword() != null,
        profileChanged
    );

    return userDto;
  }

  //  role 변경은 도메인 메서드로 진행
  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateRole(UserRoleUpdateRequest userRoleUpdateRequest) {
    if (userRoleUpdateRequest == null) {
      throw new IllegalArgumentException("userRoleUpdateRequest is null.");
    }
    User user = userRepository.findDetailById(userRoleUpdateRequest.userId())
        .orElseThrow(() -> UserNotFoundException.withUserId(userRoleUpdateRequest.userId()));

    user.updateRole(userRoleUpdateRequest.newRole());
    sessionManager.invalidateSessionsByUserId(user.getId());

    return userMapper.toDto(userRepository.save(user));
  }

  @Override
  @Transactional
  @PreAuthorize("#userId.equals(principal.userDto.id)")
  public void delete(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("userId is null.");
    }

    User user = userRepository.findDetailById(userId)
        .orElseThrow(() -> UserNotFoundException.withUserId(userId));

    BinaryContent profile = user.getProfile();

    userRepository.delete(user);

    sessionManager.invalidateSessionsByUserId(user.getId());

    if (profile != null) {
      UUID profileId = profile.getId();
      binaryContentRepository.delete(profile);
      deleteBinaryContentAfterCommit(profileId);
    }

    log.info("사용자 삭제 완료: userId={}", userId);
  }

  private boolean isUniqueUsername(String username) {
    if (username == null) {
      throw new IllegalArgumentException("username is null.");
    }
    return !userRepository.existsByUsername(username);
  }

  private boolean isUniqueEmail(String email) {
    if (email == null) {
      throw new IllegalArgumentException("email is null.");
    }

    return !userRepository.existsByEmail(email);
  }

  private BinaryContent createProfile(Optional<BinaryContentCreateRequest> profileImage) {
    BinaryContent binaryContent = null;

    if (profileImage.isPresent()) {
      BinaryContentCreateRequest binaryContentCreateRequest = profileImage.get();

      binaryContent = binaryContentRepository.save(
          binaryContentMapper.toEntity(binaryContentCreateRequest));
      binaryContentStorage.put(
          binaryContent.getId(),
          binaryContentCreateRequest.bytes(),
          binaryContent.getContentType()
      );

      log.info(
          "프로필 이미지 업로드 완료: binaryContentId={}, contentType={}, size={}",
          binaryContent.getId(),
          binaryContent.getContentType(),
          binaryContent.getSize()
      );
    }

    return binaryContent;
  }

  private void deleteBinaryContentAfterCommit(UUID binaryContentId) {
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      binaryContentStorage.delete(binaryContentId);
      return;
    }

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            binaryContentStorage.delete(binaryContentId);
          }
        }
    );
  }
}
