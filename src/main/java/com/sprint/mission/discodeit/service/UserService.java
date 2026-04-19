package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserDto create(UserCreateRequest request);
    UserDto findById(UUID id);
    UserDto findByUsername(String username);
    UserDto findByEmail(String email);
    List<UserDto> findAll();
    UserDto update(UUID id, UserUpdateRequest request);
    boolean delete(UUID id);
}
