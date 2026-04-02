package com.sprint.mission.discodeit.service.basic;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

public class BasicChannelService implements ChannelService {
	private final ChannelRepository cr;

	public BasicChannelService(ChannelRepository cr) {
		this.cr = cr;
	}

	@Override
	public Channel create(Channel channel) {
		if (!cr.existsById(channel.getId())) {
			cr.save(channel);
			return channel;
		}
		return null;
	}

	@Override
	public Channel find(UUID id) {
		return cr.findById(id).orElse(null);
	}

	@Override
	public Collection<Channel> findAll() {
		return cr.findAll();
	}

	@Override
	public Channel update(UUID id, User creator, String name, String description) {
		Optional<Channel> channel = cr.findById(id);
		if (channel.isPresent() && channel.get().getCreator().equals(creator.getId())) {
			channel.get().update(name, description);
			cr.save(channel.get());
			return channel.get();
		}
		return null;
	}

	@Override
	public void delete(UUID id, User admin, MessageService messageService) {
		Optional<Channel> channel = cr.findById(id);
		if (channel.isPresent() && channel.get().getCreator().equals(admin.getId())) {
			cr.delete(id);
			for (Message message : messageService.findAll()) {
				if (message.getChannelId().equals(channel.get().getId())) {
					messageService.delete(message.getId(), message.getUserId());
				}
			}
		}
	}
}
