package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    private final String filePath = "channels.dat";
    private Map<UUID, Channel> data;

    public FileChannelService() {
        this.data = loadFromFile();
    }

    private Map<UUID, Channel> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Map<UUID, Channel>) ois.readObject();
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
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        saveToFile();
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, Channel channel) {
        if (!data.containsKey(id)) return null;

        data.put(id, channel);
        saveToFile();
        return channel;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveToFile();
    }
}