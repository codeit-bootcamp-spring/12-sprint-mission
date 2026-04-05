package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.List;

public interface ChannelService {
    Channel create(Channel channel);
    ChannelRepository read(String channelName);
    List<Channel> readAll();
    void delete(String channelName);

    Channel create(String channelName, String description, String category, String purpose, String accessToken);
}
