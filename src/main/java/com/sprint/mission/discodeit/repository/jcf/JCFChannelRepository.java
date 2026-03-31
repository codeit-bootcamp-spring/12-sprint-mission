package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
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
    public Optional<Channel> findByName(String name) {
        for (Channel ch : data.values()) {
            if (ch.getName().equals(name)) {
                return Optional.of(ch);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel delete(UUID id) {
        Channel removed = data.remove(id);

        if (removed == null) {
            throw new IllegalArgumentException("해당 id를 가진 채널 없음.");
        }

        return removed;
    }
}


