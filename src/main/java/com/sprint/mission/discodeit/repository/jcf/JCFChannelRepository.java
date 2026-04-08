package com.sprint.mission.discodeit.repository.jcf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

public class JCFChannelRepository implements ChannelRepository {
	private final Map<UUID, Channel> data;

	public JCFChannelRepository() {
		data = new HashMap<>();
	}

	@Override
	public Channel save(Channel channel) {
		data.put(channel.getId(), channel);
		return channel;
	}

	@Override
	public Optional<Channel> findById(UUID id) {
		return Optional.ofNullable(data.get(id));
	}

	@Override
	public List<Channel> findAll() {
		return data.values().stream().toList();
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
