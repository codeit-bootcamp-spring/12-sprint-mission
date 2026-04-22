package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusCreateRequest dto);
    ReadStatusResponse findById(UUID id);
    List<ReadStatusResponse> findAllByUserId(UUID userId);
    ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest dto);
    void delete(UUID id);

}
