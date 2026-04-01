package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;

public interface ChannelService {
    Channel create(Channel channel);
    Channel read(String channelName);
    List<Channel> readAll();
    Channel update(String channelName, Channel updatedChannel);
    void delete(String channelName);
    }
