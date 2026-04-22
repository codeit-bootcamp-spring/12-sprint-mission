package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus create(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    ReadStatus update(ReadStatus readStatus);
    boolean delete(UUID id);
}
