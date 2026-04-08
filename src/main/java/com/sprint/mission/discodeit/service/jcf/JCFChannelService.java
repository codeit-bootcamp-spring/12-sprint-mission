package com.sprint.mission.discodeit.service.jcf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

public class JCFChannelService implements ChannelService {

	private final Map<UUID, Channel> data;

	public JCFChannelService() {
		this.data = new HashMap<>();
	}

	@Override
	public Channel create(Channel channel) {
		if (!data.containsKey(channel.getId())) {
			data.put(channel.getId(), channel);
			return channel;
		}
		System.err.println("이미 존재하는 채널입니다.");
		return data.get(channel.getId());
	}

	@Override
	public Channel find(UUID id) {
		return data.get(id);
	}

	@Override
	public Collection<Channel> findAll() {
		return data.values();
	}

	@Override
	public Channel update(UUID id, User creator, String name, String description) {
		Channel channel = find(id);
		if (channel != null && channel.getCreator().equals(creator.getId())) {
			channel.update(name, description);
			return channel;
		}
		System.err.println("채널을 찾을 수 없거나, 권한이 없습니다.");
		return null;
	}

	@Override
	public void delete(UUID id, User creator, MessageService messageService) {
		Channel channel = find(id);
		if (channel != null && channel.getCreator().equals(creator.getId())) {
			data.remove(channel.getId());
			for (Message message : messageService.findAll()) {
				if (message.getChannelId().equals(id)) {
					messageService.delete(message.getId(), creator.getId());
				}
			}
		} else {
			System.err.println("채널을 찾을 수 없거나, 권한이 없습니다.");
		}
	}
}
