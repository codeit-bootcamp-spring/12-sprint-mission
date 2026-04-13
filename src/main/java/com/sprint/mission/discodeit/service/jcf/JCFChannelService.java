package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data = new ConcurrentHashMap<>();

    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel find(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
    }

    @Override
    public List<Channel> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public Channel update(UUID id, ChannelType type, String name, String description) {
        Channel channel = find(id);
        channel.update(type, name, description);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("Channel with id " + id + " not found");
        }
        data.remove(id);
    }
}