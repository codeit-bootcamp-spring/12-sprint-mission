package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

public interface BinaryContentService {
	BinaryContent create(BinaryContentCreateRequestDto dto);

	BinaryContent find(UUID id);

	List<BinaryContent> findAllByIdIn(UUID id);

	void delete(UUID id);
}
