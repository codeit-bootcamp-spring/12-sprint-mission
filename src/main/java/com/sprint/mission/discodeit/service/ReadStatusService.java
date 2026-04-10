package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.ReadStatuscreateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatuscreateRequest request);

    ReadStatusResponse find(UUID id);

    List<ReadStatusResponse> finAllByUserId(UUID userId);

    ReadStatusResponse update(ReadStatusUpdateRequest request);

    void delete(UUID id);
}
