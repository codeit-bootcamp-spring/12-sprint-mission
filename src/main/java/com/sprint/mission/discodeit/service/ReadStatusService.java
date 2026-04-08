package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

public interface ReadStatusService {
	ReadStatus create(ReadStatusCreateRequestDto dto);
	ReadStatus find(UUID id);
	List<ReadStatus> findAllByUserId(UUID userId);
	ReadStatus update(ReadStatusUpdateRequestDto dto);
	void delete(UUID id);
}
