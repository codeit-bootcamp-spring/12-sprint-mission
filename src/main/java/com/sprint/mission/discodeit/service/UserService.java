package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse create(UserCreateRequest request, MultipartFile profile);

    List<UserResponse> findAll();

    UserResponse update(UUID userId, UserUpdateRequest request, MultipartFile profile);

    void delete(UUID id);
}
