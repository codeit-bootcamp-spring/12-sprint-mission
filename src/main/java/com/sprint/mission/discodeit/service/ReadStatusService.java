package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.status.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.status.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.status.ReadStatuscreateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatuscreateRequest request);

    ReadStatusResponse find(UUID id);

    List<ReadStatusResponse> findAllByUserId(UUID userId);

    ReadStatusResponse update(ReadStatusUpdateRequest request);

    void delete(UUID id);
}
