package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public JCFChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel save(Channel channel) {
        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public void update(UUID channelId, String title) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널");
        }
        channel.update(title);
        channelRepository.update(channel);
    }

    @Override
    public void delete(UUID channelId) {
        channelRepository.delete(channelId);
    }
}