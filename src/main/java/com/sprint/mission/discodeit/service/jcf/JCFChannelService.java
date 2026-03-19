package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private List<Channel> data;

    public JCFChannelService(List<Channel> data) {
        this.data = data;
    }

    @Override
    public Channel save(Channel channel) {
        return null;
    }

    @Override
    public Channel findById(UUID id) {
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return List.of();
    }

    @Override
    public Channel update(Channel channel) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
