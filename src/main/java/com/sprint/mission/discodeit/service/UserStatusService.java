package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.status.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.status.UserStatusResponse;
import com.sprint.mission.discodeit.dto.status.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create (UserStatusCreateRequest request);
    UserStatusResponse find (UUID id);
    List<UserStatusResponse> findAll ();
    UserStatusResponse update (UserStatusUpdateRequest request);
    UserStatusResponse updateByUserId (UUID userId, UserStatusUpdateRequest request);
    void delete (UUID id);
}
