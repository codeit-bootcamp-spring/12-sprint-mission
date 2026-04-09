package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public ChannelRepository read(String channelName) {
        return data.values().stream()
                .filter(c -> c.getName().equals(channelName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(String channelName, Channel updatedChannel) {
        for (Map.Entry<UUID, Channel> entry : data.entrySet()) {
            if (entry.getValue().getName().equals(channelName)) {
                data.put(entry.getKey(), updatedChannel);
                return updatedChannel;
            }
        }
        return null;
    }

    @Override
    public void delete(String channelName) {
        data.entrySet().removeIf(entry ->
                entry.getValue().getName().equals(channelName));

    }
}

