package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final Path CHANNEL_PATH = Path.of("data","channels");

    public FileChannelService() {
        FileUtils.createDirectories(CHANNEL_PATH);
    }

    @Override
    public Channel save(Channel channel) {
        Path path = CHANNEL_PATH.resolve(channel.getId().toString() + ".ser");
        FileUtils.saveObject(path, channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Path path = CHANNEL_PATH.resolve(id.toString() + ".ser");
        return (Channel) FileUtils.loadObject(path);
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        if(Files.exists(CHANNEL_PATH)) {
            return channels;
        }
        try {
            Files.list(CHANNEL_PATH).forEach(file -> {
                channels.add((Channel) FileUtils.loadObject(file));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channels;
    }

    @Override
    public Channel update(Channel channel) {
        Path path = CHANNEL_PATH.resolve(channel.getId().toString() + ".ser");
        if(!Files.exists(path)) {
            throw new RuntimeException("Channel not found.");
        }
        FileUtils.saveObject(path, channel);
        return channel;
    }

    @Override
    public Channel delete(UUID id) {
        Channel channel = findById(id);
        Path path = CHANNEL_PATH.resolve(id.toString() + ".ser");
        try {
            Files.delete(path);
            return channel;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete channel.");
        }
    }
}
