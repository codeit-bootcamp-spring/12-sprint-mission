package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.DTO.CreateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.util.FileSerialization;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private static final String FILE_PATH = "channel.ser";

    private final Map<UUID, Channel> data;

    public FileChannelService() {
        this.data = new HashMap<>();

        List<Channel> channelList = FileSerialization.<Channel>loadData(FILE_PATH);
        for (Channel ch : channelList) {
            data.put(ch.getId(), ch);
        }
    }

    @Override
    public Channel createChannel(CreateChannelRequest request) {
        Channel channel = new Channel(request.channelOwnerId(), request.type(), request.name(), request.isPrivate());

        data.put(channel.getId(), channel);
        FileSerialization.<Channel>saveData(FILE_PATH, data.values().stream().toList());
        return channel;
    }

    @Override
    public Channel findChannelByName(String name) {
        for (Channel ch : data.values()) {
            if (ch.getName().equals(name)) {
                return ch;
            }
        }

        throw new IllegalArgumentException("해당 이름의 채널 없음.");
    }

    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel changeChannelName(UUID id, String name) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("해당 id를 가진 채널 없음.");
        }

        channel.update(channel.getChannelOwnerId(), channel.getType(), name, channel.isPrivate());
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID id) {
        Channel removed = data.remove(id);

        if (removed == null) {
            throw new IllegalArgumentException("해당 id를 가진 채널 없음.");
        }

        return removed;
    }
}