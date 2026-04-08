package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> data;

    public JCFChannelRepository() { data = new ArrayList<>(); }

    @Override
    public void save(Channel channel) {
        findById(channel.getId()).ifPresent(data::remove);
        data.add(channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return data.stream().filter(channel -> channel.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<List<Channel>> findAll() {
        return Optional.of(data);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresent(data::remove);
    }
}
