package com.sprint.mission.discodeit.repository.jcfimpl;

import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }


    @Override
    public ReadStatus save(ReadStatus readStatus) {
        this.data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<ReadStatus> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {

        return findAll().stream()
                  .filter(status -> status.getUserId().equals(userId)
                          && status.getChannelId().equals(channelId))
                  .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAll().stream()
                        .filter(status -> status.getUserId().equals(userId)).toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(status -> status.getChannelId().equals(channelId)).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
}
