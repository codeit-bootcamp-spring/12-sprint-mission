package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "channels");
        FileUtils.createDirectories(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Channel save(Channel channel) {
        Path path = makePath(channel.getId());
        boolean result = FileUtils.saveObject(path, channel);
        if (!result) {
            throw new IllegalStateException("Could not save channel");
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = (Channel) FileUtils.loadObject(makePath(id));
        return Optional.ofNullable(channel);
    }

    @Override
    public List<Channel> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (Channel) FileUtils.loadObject(path))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("채널 목록을 조회할 수 없습니다.");
        }
    }


    @Override
    public void delete(UUID id) {
        Path path = makePath(id);
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제 할 수 없습니다.");
        }
    }
}
