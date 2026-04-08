package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.util.FileSerialization;

import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private static final String FILE_PATH  = "channel.ser";

    private final Map<UUID, Channel> data;

    public FileChannelRepository() {
        this.data = new HashMap<>();

        List<Channel> channelList = FileSerialization.loadData(FILE_PATH);
        for (Channel ch : channelList) {
            data.put(ch.getId(), ch);
        }
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

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

        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

        return removed;
    }
}