package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
//@Repository("fileChannelRepository")
public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository(){
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "Channels");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id){
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    @Override
    public Channel save(Channel channel) {
        Path path = makePath(channel.getId());
        boolean result = FileUtils.saveObject(path, channel);
        if(!result){
            throw new IllegalStateException("저장에 실패했습니다.");
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable((Channel)FileUtils.loadObject(makePath(id)));
    }

    @Override
    public List<Channel> findAll() {
        return List.of();
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = makePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = makePath(id);
        if(!Files.exists(path)){
            throw new RuntimeException("삭제 실패: 해당 " + id + "의 파일을 찾을 수 없습니다.");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제할 수 없습니다.");
        }
    }
}
