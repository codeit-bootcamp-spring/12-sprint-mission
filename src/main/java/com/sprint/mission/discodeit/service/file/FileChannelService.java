package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final String FILE_PATH = "channel.dat";

    public FileChannelService() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            saveAll(new ArrayList<>());
        }
    }

    private void saveAll(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException("채널 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Channel> loadAll() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Channel save(Channel channel) {
        List<Channel> channels = loadAll();
        channels.add(channel);
        saveAll(channels);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return loadAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return loadAll();
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        List<Channel> channels = loadAll();
        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
                channel.update(name, description);
                saveAll(channels);
                return channel;
            }
        }
        return null;
    }

    @Override
    public Channel delete(UUID id) {
        List<Channel> channels = loadAll();
        Channel target = channels.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (target != null) {
            channels.remove(target);
            saveAll(channels);
        }
        return target;
    }
}