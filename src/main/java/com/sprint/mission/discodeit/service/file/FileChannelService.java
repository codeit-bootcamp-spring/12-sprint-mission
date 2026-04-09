package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public FileChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel save(Channel channel) {
        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널: " + channelId);
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public void update(UUID channelId, String title) {
        Channel channel = findById(channelId);
        channel.update(title);
        channelRepository.update(channel);
    }

    @Override
    public void delete(UUID channelId) {
        channelRepository.delete(channelId);
    }
}