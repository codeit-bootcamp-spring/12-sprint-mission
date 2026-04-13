package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.channel.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.FileSerialization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {
    private static final String FILE_PATH = "ReadStatus.ser";

    private final Map<UUID, ReadStatus> data;

    public FileReadStatusRepository() {
        this.data = new HashMap<>();

        List<ReadStatus> readStatusList = FileSerialization.loadData(FILE_PATH);
        for (ReadStatus readStatus : readStatusList) {
            data.put(readStatus.getId(), readStatus);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        FileSerialization.saveData(FILE_PATH, new ArrayList<>(data.values()));
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();

        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getUserId().equals(userId)) {
                result.add(readStatus);
            }
        }

        return result;
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        List<ReadStatus> result = new ArrayList<>();

        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getChannelId().equals(channelId)) {
                result.add(readStatus);
            }
        }

        return result;
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public ReadStatus deleteById(UUID id) {
        ReadStatus removed = data.remove(id);

        if (removed == null) {
            throw new IllegalArgumentException("해당 id를 가진 ReadStatus 없음.");
        }

        FileSerialization.saveData(FILE_PATH, new ArrayList<>(data.values()));
        return removed;
    }

    @Override
    public ReadStatus deleteByUserId(UUID userId) {
        List<UUID> targetIds = new ArrayList<>();
        ReadStatus firstRemoved = null;

        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getUserId().equals(userId)) {
                targetIds.add(readStatus.getId());

                if (firstRemoved == null) {
                    firstRemoved = readStatus;
                }
            }
        }

        if (firstRemoved == null) {
            throw new IllegalArgumentException("해당 userId를 가진 ReadStatus 없음.");
        }

        for (UUID id : targetIds) {
            data.remove(id);
        }

        FileSerialization.saveData(FILE_PATH, new ArrayList<>(data.values()));
        return firstRemoved;
    }

    @Override
    public ReadStatus deleteByChannelId(UUID channelId) {
        List<UUID> targetIds = new ArrayList<>();
        ReadStatus firstRemoved = null;

        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getChannelId().equals(channelId)) {
                targetIds.add(readStatus.getId());

                if (firstRemoved == null) {
                    firstRemoved = readStatus;
                }
            }
        }

        if (firstRemoved == null) {
            throw new IllegalArgumentException("해당 channelId를 가진 ReadStatus 없음.");
        }

        for (UUID id : targetIds) {
            data.remove(id);
        }

        FileSerialization.saveData(FILE_PATH, new ArrayList<>(data.values()));
        return firstRemoved;
    }
}