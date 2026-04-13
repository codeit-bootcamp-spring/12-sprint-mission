package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sprint.mission.discodeit.entity.channel.ReadStatus;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);

    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    List<ReadStatus> findAll();

    ReadStatus deleteById(UUID id);
    ReadStatus deleteByUserId(UUID channelId);
    ReadStatus deleteByChannelId(UUID channelId);
}
