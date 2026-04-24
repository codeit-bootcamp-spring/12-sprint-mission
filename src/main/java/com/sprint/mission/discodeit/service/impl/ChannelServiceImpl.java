package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;

    public ChannelServiceImpl(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }
    @Override
    public ChannelDto create(ChannelDto channelDto) {
        return null;
    }

    @Override
    public Channel update(UUID id, ChannelDto channelDto) {
        return null;
    }

    @Override
    public ChannelDto delete(UUID id) {
        return null;
    }

    @Override
    public List<ChannelDto> findChannelsByUser(UUID userId) {
        return List.of();
    }
}
