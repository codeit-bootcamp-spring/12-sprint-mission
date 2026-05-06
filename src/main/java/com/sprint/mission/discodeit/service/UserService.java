package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {

  UserDto create(UserCreateRequest request, Optional<BinaryContentCreateRequest> profile);

  UserDto find(UUID userId);

  List<UserDto> findAll();

  UserDto update(UUID userId, UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> profile);

  void delete(UUID userId);

  Optional<Object> findById(UUID id);
}