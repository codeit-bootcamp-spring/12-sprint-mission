package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public interface MessageService {
	Message create(Channel channel, Message message);

	Message find(UUID id);

	List<Message> findAll();

	Message update(UUID id, UUID userid, String content);

	void delete(UUID id, User user);
}
