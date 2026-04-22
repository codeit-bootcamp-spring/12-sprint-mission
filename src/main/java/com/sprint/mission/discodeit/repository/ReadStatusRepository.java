package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    List<UUID> findAllByChannelId(UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);
    boolean existsById(UUID id);
    boolean existsByChannelIdAndUserId(UUID channelId, UUID userId);
    void deleteById(UUID id);
    void deleteAllByChannelId(UUID channelId);

}
