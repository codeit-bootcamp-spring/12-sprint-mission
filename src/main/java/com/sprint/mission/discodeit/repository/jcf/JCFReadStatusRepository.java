package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;

public class JCFReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> data = new HashMap<>();

    @Override
    public void save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
    }

    @Override
    public ReadStatus findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .toList();
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return data.values().stream()
                .filter(status -> status.getUserId().equals(userId)
                        && status.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}