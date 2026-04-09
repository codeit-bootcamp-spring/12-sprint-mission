package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

@Repository("channelRepository")
public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"),"my_dir","channels");
        FileUtils.createDirectories(DIRECTORY);
    }
    private Path makePath(UUID id){
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Channel save(Channel channel) {
        Path path = makePath(channel.getId());
        boolean result = FileUtils.saveObject(path,channel);
        if(!result){
            throw new IllegalStateException("Could not save channel");
        }
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return (Channel) FileUtils.loadObject(makePath(id));
    }

    @Override
    public List<Channel> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (Channel)FileUtils.loadObject(path))
                    .toList();
        } catch (Exception e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public Channel update(Channel channel) {
        Path path = makePath(channel.getId());
        if(!Files.exists(path)) {
            throw new NoSuchElementException("수정할 채널이 없습니다.");
        }
        return save(channel);
    }

    @Override
    public Channel delete(UUID id) {
        Path path = makePath(id);
        if (!Files.exists(path)) {
            return  null;
        }
        Channel channelDel = (Channel) FileUtils.loadObject(path);
        try {
            Files.delete(path);
            return channelDel;
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제 할 수 없습니다.");
        }
    }
}
