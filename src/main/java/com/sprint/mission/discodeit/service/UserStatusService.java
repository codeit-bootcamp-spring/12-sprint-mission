package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto create(UserStatusCreateRequest request);

    UserStatusDto findById(UUID id);

    List<UserStatusDto> findAll();

    UserStatusDto update(UserStatusUpdateRequest request);

    UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

    void delete(UUID id);
}
