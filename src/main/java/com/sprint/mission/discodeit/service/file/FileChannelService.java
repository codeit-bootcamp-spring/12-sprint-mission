package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelService /*implements ChannelService*/ {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelService(){
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "Channels");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id){
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }


    //@Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        Path path = makePath(channel.getId());
        boolean result = FileUtils.saveObject(path, channel);
        if(!result){
            throw new IllegalStateException("채널 저장에 실패했습니다.");
        }
        return channel;
    }

    //@Override
    public Channel find(UUID channelId) {
        return (Channel)FileUtils.loadObject(makePath(channelId));
    }

    //@Override
    public List<Channel> findAll() {
        return FileUtils.load(DIRECTORY);
    }

    //@Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        return null;
    }


    //@Override
    public void delete(UUID id) {
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

    public void deleteAll() {
        try(Stream<Path> stream = Files.list(DIRECTORY)) {
            stream.filter(path ->  path.getFileName().toString().endsWith(EXTENSION))
                    .forEach(path ->{
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
