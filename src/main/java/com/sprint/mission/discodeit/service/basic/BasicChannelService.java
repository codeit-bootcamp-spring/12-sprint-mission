package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.CreateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(CreateChannelRequest request) {
        Channel channel = new Channel(request.channelOwnerId(), request.type(), request.name(), request.isPrivate());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel findChannelByName(String name) {
        return channelRepository.findChannelByName(name);
    }

    @Override
    public List<Channel> findAllChannels() {
        return channelRepository.findAllChannels();
    }

    @Override
    public Channel changeChannelName(UUID id, String name) {
        return channelRepository.changeChannelName(id, name);
    }

    @Override
    public Channel deleteChannel(UUID id) {
        return channelRepository.deleteChannel(id);
    }
}