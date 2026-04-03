package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        if (channel == null) {
            return null;
        }

        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) { // 객체 리턴하는 메소드들에 옵셔널 도입하는 방법 고려중(공부중), 도입하는게 나을지? 어느부분에서 사용하는게 정석적인지
        if (id == null) {
            return null;
        }

        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = findById(id);

        if (channel != null) {
            channel.update(name, description);
            return channel;
        }

        return null;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
