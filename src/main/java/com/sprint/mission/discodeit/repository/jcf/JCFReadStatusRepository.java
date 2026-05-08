package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "spring.service.type", havingValue = "jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID,ReadStatus> data = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        if(readStatus == null){
            throw new NoSuchElementException("ReadStatus 객체가 비어있습니다.");
        }
        if(readStatus.getId() == null){
            throw new NoSuchElementException("ReadStatus ID를 찾을 수 없습니다.");
        }
        data.put(readStatus.getId(),readStatus);
        return data.get(readStatus.getId());
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
    public List<ReadStatus> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(status->status.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(status->status.getUserId().equals(userId))
                .toList();
    }

    @Override
    public Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        return data.values().stream()
                .filter(status->
                        status.getChannelId().equals(channelId) && status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
