package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);
    Optional<BinaryContent> findById(UUID id);
    Optional<BinaryContent> findByUserId(UUID userId);
    List<UUID> findByMessageId(UUID messageId);
    List<BinaryContent> findAll();
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    void deleteByUserId(UUID userId);
    void deleteByMessageId(UUID messageId);
}
