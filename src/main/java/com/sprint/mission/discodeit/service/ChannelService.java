package com.sprint.mission.discodeit.service;

import java.util.Collection;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

public interface ChannelService {
	Channel create(Channel channel);

	Channel find(UUID id);

	Collection<Channel> findAll();

	Channel update(UUID id, User creator, String name, String description);

	void delete(UUID id, User admin, MessageService messageService);
}
