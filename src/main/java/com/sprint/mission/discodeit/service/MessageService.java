package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageService {
	Message save(Message message);
	Message findById(UUID id);
	List<Message> findAll();
	Message update(UUID id, UUID userid, String content);
	void deleteById(UUID id);
}
