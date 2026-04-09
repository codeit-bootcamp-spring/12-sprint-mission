package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel save(Channel channel) {
        if (userService.findById(channel.getAuthor().getId()) == null) {
            System.out.println("Author가 존재하지 않습니다.");
            return null;
        }
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        if (data.containsKey(id)) {
            return data.get(id);
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(Channel channel) {
        Channel updateChannel = findById(channel.getId());
        if(updateChannel == null){
            return null;
        }
        updateChannel.update(channel);
        return updateChannel;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
