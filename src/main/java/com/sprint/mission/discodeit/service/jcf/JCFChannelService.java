package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

public class JCFChannelService implements ChannelService {

	private final List<Channel> data;

	public JCFChannelService() {
		this.data = new ArrayList<>();
	}

	@Override
	public Channel create(Channel channel) {
		data.add(channel);
		return channel;
	}

	@Override
	public Channel find(UUID id) {
		return data.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public List<Channel> findAll() {
		return data;
	}

	@Override
	public Channel update(UUID id, User creator, String name, String description) {
		Channel channel = find(id);
		if (channel != null && channel.getCreator().equals(creator.getId())) {
			channel.update(name,  description);
			return channel;
		}

		return null;
	}

	@Override
	public void delete(UUID id, User creator,  MessageService messageService) {
		Channel channel = find(id);
		if (channel != null && channel.getCreator().equals(creator.getId())) {
			data.remove(channel);
			for (Message message : messageService.findAll()) {
				if (message.getChannelId().equals(id)) {
					messageService.delete(message.getId(), creator);
				}
			}
		}
	}
}
