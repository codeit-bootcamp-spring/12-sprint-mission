package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

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
    public Channel update(UUID id, String name, String description) {
        return null;
    }

    @Override
    public Channel delete(UUID id) {
        return null;
    }
}
