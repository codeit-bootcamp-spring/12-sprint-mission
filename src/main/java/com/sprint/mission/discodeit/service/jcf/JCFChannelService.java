package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

public class JCFChannelService implements ChannelService {

	private final List<Channel> data;

	public JCFChannelService() {
		this.data = new ArrayList<>();
	}

	@Override
	public Channel save(Channel channel) {
		data.add(channel);
		return channel;
	}

	@Override
	public Channel findById(UUID id) {
		for(Channel channel : data) {
			if(channel.getId().equals(id)) {
				return channel;
			}
		}
		return null;
	}

	@Override
	public List<Channel> findAll() {
		return data;
	}

	@Override
	public Channel update(UUID id, String name, List<User> users, List<Message> messages) {
		Channel  channel = findById(id);
		if(channel != null) {
			channel.update(name, users, messages);
			return channel;
		}else{
			return null;
		}
	}

	@Override
	public void deleteById(UUID id) {
		data.remove(findById(id));
	}
}
