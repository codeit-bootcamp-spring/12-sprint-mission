package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sprint.mission.discodeit.entity.channel.Channel;

public interface ChannelRepository {
    Channel save(Channel channel);

    Optional<Channel> findById(UUID id);
    Optional<Channel> findByName(String name);
    List<Channel> findAll();

    Channel deleteById(UUID id);
}