package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto create(BinaryContentCreateRequest request);

    BinaryContentDto findById(UUID id);

    List<BinaryContentDto> findAllByIdIn(List<UUID> id);

    void delete(UUID id);
}
