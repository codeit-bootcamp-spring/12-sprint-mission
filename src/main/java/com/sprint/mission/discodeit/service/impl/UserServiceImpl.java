package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User create(UserDto userDto) {

    User user = new User();
    user.setUsername(userDto.username());
    user.setEmail(userDto.email());
    user.setProfileId(userDto.profileId());
    user.setOnline(userDto.online());
    user.setCreatedAt(userDto.createdAt());
    user.setUpdatedAt(userDto.updatedAt());
    return userRepository.save(user);
  }

  @Override
  public UserDto find(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return convertToDto(user);
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  public User update(UUID userId, UserDto userDto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.setUsername(userDto.username());
    user.setEmail(userDto.email());
    user.setProfileId(userDto.profileId());
    user.setOnline(userDto.online());
    user.setCreatedAt(userDto.createdAt());
    user.setUpdatedAt(userDto.updatedAt());
    return userRepository.save(user);
  }

  @Override
  public UserDto delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    userRepository.delete(user);
    return convertToDto(user);
  }

  @Override
  public User updateOnlineStatus(UUID userId, Boolean online) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    user.setOnline(online);
    return userRepository.save(user);
  }

  @Override
  public User login(String username, String password) {
    User user = (User) userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Login failed"));

    if (!user.getPassword().equals(password)) {
      throw new RuntimeException("Login failed");
    }

    return user;
  }

  @Override
  public UserDto createUser(UserCreateRequest request, MultipartFile profile) {

    User user = new User
    (request.getUsername(),
    request.getEmail(),
    request.getPassword());

    User saved = userRepository.save(user);
    return toDto(saved);
  }

  private UserDto toDto(User saved) {
    return null;
  }

  // Entity → DTO 변환 메서드
  private UserDto convertToDto(User user) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        user.getOnline()
    );
  }

}
