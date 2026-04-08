package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageRepository {
	Message save(Message message);
	Optional<Message> findById(UUID id);
	List<Message> findAll();
	Long count();
	void delete(UUID id);
	boolean existsById(UUID id);
}
