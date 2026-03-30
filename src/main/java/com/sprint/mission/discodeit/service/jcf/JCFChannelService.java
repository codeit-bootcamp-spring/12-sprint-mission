package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    // 의존성 주입
    public JCFChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(String name) {
        Channel channel = new Channel(name);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID id, String name) {
        Channel channel = channelRepository.findById(id);
        if (channel != null) {
            channel.update(name);
            channelRepository.update(channel);
        }
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        channelRepository.delete(id);
    }
}