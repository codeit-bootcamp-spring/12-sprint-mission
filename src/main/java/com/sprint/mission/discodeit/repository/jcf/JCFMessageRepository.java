package com.sprint.mission.discodeit.repository.jcf;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

public class JCFMessageRepository implements MessageRepository {
	private final Map<UUID, Message> data;

	public JCFMessageRepository() {
		data = new HashMap<>();
	}

	@Override
	public Message save(Message message) {
		data.put(message.getId(), message);
		return message;
	}

	@Override
	public Optional<Message> findById(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public List<Message> findAll() {
		return data.values().stream().sorted(Comparator.comparing(Message::getCreatedAt)).toList();
	}

	@Override
	public Long count() {
		return (long)data.size();
	}

	@Override
	public void delete(UUID id) {
		data.remove(id);
	}

	@Override
	public boolean existsById(UUID id) {
		return data.containsKey(id);
	}
}
