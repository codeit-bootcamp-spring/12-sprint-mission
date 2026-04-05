package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(Channel channel);
    Channel getChannel(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, String channelName);
    void delete(UUID id);

    // 추가 ?
    void join(UUID channelId, User user);
    void leave(UUID channelId, User user);
}
