package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.data.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.data.dto.UserDto;
import com.sprint.mission.discodeit.data.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.user.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest request);

    UserDto find(UUID userId);
    List<UserDto> findAll();

    User update(UUID id, UserUpdateRequest request);

    User delete(UUID userId);
}
