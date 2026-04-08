package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelSevice implements ChannelService {

    private final ChannelRepository repository;

    public BasicChannelSevice(ChannelRepository repository){
        this.repository = repository;
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        repository.save(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(UUID id, ChannelType type, String name, String description) {
        Channel channel = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널 없음"));
        channel.update(type, name, description);
        repository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}
