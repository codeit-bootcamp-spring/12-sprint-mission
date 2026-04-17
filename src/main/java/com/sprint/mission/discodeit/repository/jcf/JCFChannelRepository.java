package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> data;

    public JCFChannelRepository() {
        data = new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.add(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        for(Channel channel : data) {
            if(channel.getId().equals(id)) return Optional.of(channel);
        }
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() { return List.copyOf(data); }

    @Override
    public Channel update(UUID id, Channel channel) {
        Optional<Channel> OptionalChannel = findById(id);
        if (OptionalChannel.isPresent()) {
            Channel found = OptionalChannel.get();
            found.update(channel.getName(), channel.isPrivate());
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
