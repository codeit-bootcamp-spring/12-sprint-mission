package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    /// Create
    UUID create(Channel channel);

    /// Read
    Optional<Channel> findById(UUID id);
    Optional<List<Channel>> findAll();

    /// Update
    void updateById(UUID id, String channelName, String description, User owner);
    void addUser(UUID id, User user);
    void deleteUser(UUID id, User user);
    void addMessage(UUID id, Message message);
    void deleteMessage(UUID id, Message message);

    /// Delete
    void deleteById(UUID id);
}
