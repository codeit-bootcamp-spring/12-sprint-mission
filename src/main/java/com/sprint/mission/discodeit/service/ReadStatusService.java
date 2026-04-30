package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.data.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.data.readstatus.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusCreateRequest request);
    ReadStatusResponse find(UUID id);
    List<ReadStatusResponse> findAllByUserId(UUID userId);
    ReadStatusResponse update(UUID id, ReadStatusUpdateRequest request);
    void delete(UUID id);
}