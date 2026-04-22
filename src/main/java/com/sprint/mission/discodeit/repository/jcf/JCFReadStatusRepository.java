package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;
@Primary
@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data = new HashMap<>();


    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public Optional<ReadStatus> findByChannelId(UUID channelId, UUID userId) {
        return data.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId) && rs.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);

    }
}
