package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelService implements ChannelService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final UserService userService;

    public FileChannelService(UserService userService) {
        this.userService = userService;
        this.DIRECTORY = Path.of(System.getProperty("user.dir"),"data","channels");
        if(Files.notExists(DIRECTORY)){
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Channel save(Channel channel) {
        if(userService.findById(channel.getAuthor().getId()) == null){
            System.out.println("Author is not exist.");
            return null;
        }

        Path path = makePath(channel.getId());
        boolean result = false;
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            oos.writeObject(channel);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        if(!result){
            throw new IllegalStateException("Could not save Channel");
        }
        return channel;
    }

    public Channel loadChannels(Path path){
        if(Files.notExists(path)){
            return null;
        }
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);
        ){
            return (Channel)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("File io error");
        }
    }

    @Override
    public Channel findById(UUID id) {
        Path path =makePath(id);
        return loadChannels(path);
    }

    @Override
    public List<Channel> findAll() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(path -> (Channel)loadChannels(path))
                    .sorted()
                    .toList();
        } catch (Exception e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public Channel update(Channel channel) {
        Path path = makePath(channel.getId());

        Channel oldChannel = loadChannels(path);

        if(channel.getTitle() != null) oldChannel.updateTitle(channel.getTitle());
        if(channel.getCategory() != null) oldChannel.updateCategory(channel.getCategory());

        save(oldChannel);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Path path = makePath(id);
        if(Files.notExists(path)){
            return;
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제할 수 없습니다.");
        }
    }
}
