package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelMap = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        channelMap.put(channel.getChannelId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return channelMap.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void deleteById(UUID id) {
        channelMap.remove(id);
    }
}
