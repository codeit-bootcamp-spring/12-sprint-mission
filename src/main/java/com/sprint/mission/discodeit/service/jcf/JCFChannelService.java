package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        if (channel == null) {
            return null;
        }

        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        if (id == null) {
            return null;
        }

        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = findById(id);

        if (channel != null) {
            channel.update(name, description);
            return channel;
        }

        return null;
    }

    @Override
    public Channel delete(UUID id) {
        return data.remove(id);
    }
}
