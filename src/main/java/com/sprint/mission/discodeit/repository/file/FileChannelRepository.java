package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "channels");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Channel save(Channel channel) {
        Path path = makePath(channel.getId());
        try {
            FileUtils.saveObject(path, channel);
        } catch (RuntimeException e) {
            throw new RuntimeException("채널 저장 실패");
        }
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return (Channel) FileUtils.loadObject(makePath(channelId));
    }

    @Override
    public List<Channel> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (Channel) FileUtils.loadObject(path))
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new NoSuchElementException("경로를 찾을 수 없음");
        }
    }

    @Override
    public void update(Channel channel) {
        save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Path path = makePath(channelId);

        if (!Files.exists(path)) {
            return;
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 실패");
        }
    }
}