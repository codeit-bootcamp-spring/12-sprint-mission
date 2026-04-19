package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.*;
import java.util.*;

public class FileReadStatusRepository implements ReadStatusRepository {

    private final String filePath;
    private Map<UUID, ReadStatus> data;

    public FileReadStatusRepository(String directory) {
        new File(directory).mkdirs();
        this.filePath = directory + "/read-status.dat";
        this.data = loadFromFile();
    }

    private Map<UUID, ReadStatus> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Map<UUID, ReadStatus>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        saveToFile();
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
        saveToFile();
    }
}