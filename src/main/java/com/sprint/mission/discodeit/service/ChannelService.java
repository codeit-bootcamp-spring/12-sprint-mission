package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public interface ChannelService {
	Channel save(Channel channel);
	Channel findById(UUID id);
	List<Channel> findAll();
	Channel update(UUID id, String name, List<User> users, List<Message> messages);
	void deleteById(UUID id);
}
