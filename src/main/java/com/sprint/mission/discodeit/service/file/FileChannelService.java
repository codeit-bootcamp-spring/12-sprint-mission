package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private static final String FILE_PATH = "channels.ser";

    private final Map<UUID, Channel> channelRepo;

    public FileChannelService() {
        this.channelRepo = loadFromFile();
    }

    @Override
    public Channel create(Channel channel) {
        channelRepo.put(channel.getId(), channel);
        saveToFile();
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        return channelRepo.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelRepo.values());
    }

    @Override
    public Channel update(UUID id, String channelName) {
        Channel channel = channelRepo.get(id);

        if (channel != null) {
            channel.updateChannelName(channelName);
            saveToFile();
        }

        return channel;
    }

    @Override
    public void delete(UUID id) {
        channelRepo.remove(id);
        saveToFile();
    }

    @Override
    public void join(UUID channelId, User user) {
        Channel channel = channelRepo.get(channelId);

        if (channel != null) {
            channel.addParticipant(user);
            saveToFile();
        }
    }

    @Override
    public void leave(UUID channelId, User user) {
        Channel channel = channelRepo.get(channelId);

        if (channel != null) {
            channel.removeParticipant(user);
            saveToFile();
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channelRepo);
        } catch (IOException e) {
            throw new RuntimeException("Channel 저장 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadFromFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Channel 로드 실패", e);
        }
    }
}