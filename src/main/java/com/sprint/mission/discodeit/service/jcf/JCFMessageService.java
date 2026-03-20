package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

public class JCFMessageService implements MessageService {

	private final List<Message> data;

	public JCFMessageService() {
		data = new ArrayList<>();
	}
	@Override
	public Message save(Message message) {
		data.add(message);
		return message;
	}

	@Override
	public Message findById(UUID id) {
		for (Message message : data) {
			if (message.getId().equals(id)) {
				return message;
			}
		}
		return null;
	}

	@Override
	public List<Message> findAll() {
		return data;
	}

	@Override
	public Message update(UUID id, UUID userid, String content) {
		Message message = this.findById(id);
		if (message != null && message.getUserUUID().equals(userid)) {
			message.update(content);
			return message;
		}else{
			return null;
		}
	}

	@Override
	public void deleteById(UUID id) {
		data.remove(findById(id));
	}
}
