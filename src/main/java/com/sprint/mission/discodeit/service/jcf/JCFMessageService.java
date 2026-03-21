package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

public class JCFMessageService implements MessageService {

	private final List<Message> data;

	public JCFMessageService() {
		data = new ArrayList<>();
	}

	@Override
	public Message create(Channel channel, Message message) {

		if (channel == null || message == null) {
			return null;
		}
		if (channel.getId().equals(message.getChannelId())) {
			data.add(message);
			return message;
		}

		return null;
	}

	@Override
	public Message find(UUID id) {
		return data.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public List<Message> findAll() {
		return data;
	}

	@Override
	public Message update(UUID id, UUID userid, String content) {
		for (Message message : data) {
			if (message.getId().equals(id)) {
				message.update(content);
				return message;
			}
		}
		return null;
	}

	@Override
	public void delete(UUID id, User user) {
		Message message = find(id);
		if (user != null && message != null && message.getUserId().equals(user.getId())) {
			data.remove(message);
		}
	}
}
