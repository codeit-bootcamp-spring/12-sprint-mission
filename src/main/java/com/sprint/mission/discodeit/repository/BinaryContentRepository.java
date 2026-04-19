package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent create(BinaryContent binaryContent);
    Optional<BinaryContent> findById(UUID id);
    boolean delete(UUID id);
}
