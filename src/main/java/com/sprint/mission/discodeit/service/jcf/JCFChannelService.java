package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.DTO.CreateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel createChannel(CreateChannelRequest request) {
        Channel channel = new Channel(request.channelOwnerId(), request.type(), request.name(), request.isPrivate());

        data.put(channel.getId(), channel);
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