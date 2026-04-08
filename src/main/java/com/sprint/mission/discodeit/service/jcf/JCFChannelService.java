package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        data = new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        for(Channel channel : data) {
            if(channel.getId().equals(id)) return channel;
        }
        return null;
    }

    @Override
    public List<Channel> findAll() { return List.copyOf(data); }

    @Override
    public Channel update(UUID id, Channel channel) {
        Channel found = findById(id);
        if (found != null) {
            found.update(channel.getName(), channel.getIsPrivate());
            return found;
        }
        return null;
    }

    @Override
    public boolean delete(UUID id) {
        for(Channel channel : data) {
            if(channel.getId().equals(id)) {
                data.remove(channel);
                return true;
            }
        }
        return false;
    }
}
