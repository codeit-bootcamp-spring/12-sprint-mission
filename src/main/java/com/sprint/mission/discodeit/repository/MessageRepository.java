package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageRepository {
	Message save(Message message);

	Optional<Message> findById(UUID id);

	List<Message> findAll();

	List<Message> findAllByChannelId(UUID channelId);

	boolean existsById(UUID id);

	void deleteById(UUID id);
}
