package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.io.*;
import java.util.*;

public class FileUserStatusRepository implements UserStatusRepository {

    private final String filePath;
    private Map<UUID, UserStatus> data;

    public FileUserStatusRepository(String directory) {
        new File(directory).mkdirs();
        this.filePath = directory + "/user-status.dat";
        this.data = loadFromFile();
    }

    private Map<UUID, UserStatus> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Map<UUID, UserStatus>) ois.readObject();
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
    public void save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
        saveToFile();
    }

    @Override
    public UserStatus findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        return data.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveToFile();
    }
}