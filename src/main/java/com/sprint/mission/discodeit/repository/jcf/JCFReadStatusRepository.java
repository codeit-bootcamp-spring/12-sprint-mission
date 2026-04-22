package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> readStatusMap;

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatusMap.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(readStatusMap.get(id));
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(readStatusMap.values());
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID id) {
        List<ReadStatus> readStatuses = new ArrayList<>();
        for (ReadStatus readStatus : readStatusMap.values()) {
            if (readStatus.getUserId().equals(id)) {
                readStatuses.add(readStatus);
            }
        }
        return readStatuses;
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID id) {
        List<ReadStatus> readStatuses = new ArrayList<>();
        for (ReadStatus readStatus : readStatusMap.values()) {
            if (readStatus.getChannelId().equals(id)) {
                readStatuses.add(readStatus);
            }
        }
        return readStatuses;
    }

    @Override
    public void delete(UUID id) {
        readStatusMap.remove(id);
    }
}
