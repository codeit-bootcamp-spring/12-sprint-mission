package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        data = new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        for (Channel channel : data) {
            if (channel.getId().equals(channelId)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return data;
    }

    @Override
    public void update(UUID channelId, String title) {
        for (Channel channel : data) {
            if (channel.getId().equals(channelId)) {
                channel.update(title);
                break;
            }
        }
    }

    @Override
    public void delete(UUID channelId) {
        data.removeIf(channel -> channel.getId().equals(channelId));
    }
}