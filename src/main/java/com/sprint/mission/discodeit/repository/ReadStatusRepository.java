package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    void save(ReadStatus readStatus);

    List<ReadStatus> findAll();

    Optional<ReadStatus> findById(UUID id);

    List<ReadStatus> findByChannelId(UUID id);

    List<ReadStatus> findByUserId(UUID id);

    void delete(UUID id);
}
