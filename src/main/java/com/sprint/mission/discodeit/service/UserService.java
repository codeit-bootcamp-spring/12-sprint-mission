package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponse create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);
    UserResponse findById(UUID userId);
    List<UserResponse> findAll();
    UserResponse update(UUID userId, UserUpdateRequest request,
                        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);
    void delete(UUID userId);
}