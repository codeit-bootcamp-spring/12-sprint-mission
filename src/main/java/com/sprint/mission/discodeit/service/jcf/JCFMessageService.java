package com.sprint.mission.discodeit.service.jcf;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

public class JCFMessageService implements MessageService {

	private final Map<UUID, Message> data;

	public JCFMessageService() {
		data = new HashMap<>();
	}

	@Override
	public Message create(Channel channel, Message message) {

		if (channel == null || message == null) {
			return null;
		}
		if (!channel.getId().equals(message.getChannelId())) {
			return null;
		}
		if (!data.containsKey(message.getId())) {
			return data.put(message.getId(), message);
		} else {
			System.err.println("메세지가 이미 존재합니다.");
			return null;
		}
	}

	@Override
	public Message find(UUID id) {
		return data.get(id);
	}

	@Override
	public List<Message> findAll() {
		return data.values().stream().sorted(Comparator.comparing(Message::getCreatedAt)).toList();
	}

	@Override
	public Message update(UUID id, UUID userid, String content) {
		for (Message message : data.values()) {
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
			data.remove(message.getId());
		}
	}
}
