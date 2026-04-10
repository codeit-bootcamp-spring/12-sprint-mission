package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);

    List<ReadStatus> findAll();

    Optional<ReadStatus> findById(UUID id);

    List<ReadStatus> findByChannelId(UUID channelId);

    List<ReadStatus> findByUserId(UUID userId);

    ReadStatus findByChannelIdAndUserId(UUID channelId, UUID userId );

    void delete(UUID id);
}
