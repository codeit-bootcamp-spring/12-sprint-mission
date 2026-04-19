package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.*;
import java.util.*;

public class FileBinaryContentRepository implements BinaryContentRepository {

    private final String filePath;
    private Map<UUID, BinaryContent> data;

    public FileBinaryContentRepository(String directory) {
        new File(directory).mkdirs();
        this.filePath = directory + "/binary-content.dat";
        this.data = loadFromFile();
    }

    private Map<UUID, BinaryContent> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
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
    public void save(BinaryContent content) {
        data.put(content.getId(), content);
        saveToFile();
    }

    @Override
    public BinaryContent findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveToFile();
    }
}