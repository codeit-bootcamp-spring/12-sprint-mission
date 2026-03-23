package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
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


