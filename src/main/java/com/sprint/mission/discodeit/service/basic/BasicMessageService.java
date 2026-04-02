package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

public class BasicMessageService implements MessageService {
	private final MessageRepository mr;

	public BasicMessageService(MessageRepository mr) {
		this.mr = mr;
	}

	@Override
	public Message create(Channel channel, Message message) {
		if (channel == null || message == null) {
			return null;
		}
		if (!channel.getId().equals(message.getChannelId())) {
			return null;
		}
		if (!mr.existsById(message.getId())) {
			mr.save(message);
			return message;
		} else {
			return null;
		}
	}

	@Override
	public Message find(UUID id) {
		return mr.findById(id).orElse(null);
	}

	@Override
	public List<Message> findAll() {
		return mr.findAll();
	}

	@Override
	public Message update(UUID id, UUID userid, String content) {
		Optional<Message> msg = mr.findById(id);
		if (msg.isPresent() && msg.get().getId().equals(id) && msg.get().getUserId().equals(userid)) {
			msg.get().update(content);
			mr.save(msg.get());
			return msg.get();
		}
		return null;
	}

	@Override
	public void delete(UUID id, UUID userId) {
		Optional<Message> msg = mr.findById(id);
		if (msg.isPresent() && msg.get().getUserId().equals(userId)) {
			mr.delete(id);
		}
	}
}
