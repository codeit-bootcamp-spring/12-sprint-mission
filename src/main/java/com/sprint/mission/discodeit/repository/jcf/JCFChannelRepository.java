package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channels = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        if(channels.containsKey(id)){
            return channels.get(id);
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> findChannel= new ArrayList<>();
        for(Channel channel : channels.values()){
            findChannel.add(channel);
        }
        return findChannel;
//        return channels.values().stream().toList();
    }

    @Override
    public Channel update(Channel channel) {
        if(channels.containsKey(channel.getId())){
            channels.put(channel.getId(), channel);
            return channel;
        }
        return null;
    }

    @Override
    public Channel delete(UUID id) {
        Channel channel = channels.get(id);
        channels.remove(id);
        return channel;
    }
}
