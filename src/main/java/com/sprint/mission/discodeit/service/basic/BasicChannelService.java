package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;

    }

    @Override
    public Channel create(String channelName, String description, String category, String purpose, String accessToken) {
        boolean exists = channelRepository.findAll().stream()
                .anyMatch(c -> c.getName().equals(channelName));
        if (exists) {
            throw new IllegalArgumentException("channel already exists!");
        }
        Channel channel = new Channel();
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel create(Channel channel) {
        return channelRepository.save(channel);
    }

    @Override
    public ChannelRepository read(String channelName) {
        return (ChannelRepository) channelRepository.findAll().stream()
                .filter(c -> c.getName().equals(channelName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(channelRepository.findAll());
    }

    @Override
    public void delete(String channelName) {
    }
}


