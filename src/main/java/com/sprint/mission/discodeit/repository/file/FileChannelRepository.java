package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "my_dir", "channels");
        try {
            Files.createDirectories(this.DIRECTORY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public void save(Channel channel) {
        Path path = makePath(channel.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            System.out.println("채널 저장에 실패하였습니다.");
        }
    }

    @Override
    public Channel findById(UUID id) {
        Path path = makePath(id);
        if (Files.notExists(path)) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (Channel) ois.readObject();
        } catch (Exception e) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return null;
        }
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIRECTORY,
                path -> path.toString().endsWith(EXTENSION))) {
            for (Path path : stream) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                    channels.add((Channel) ois.readObject());
                } catch (Exception e) {
                    System.out.println("일부 채널을 불러오지 못했습니다.");
                }
            }
        } catch (IOException e) {
            System.out.println("채널 목록 조회 실패");
        }
        return channels;
    }

    @Override
    public void update(Channel channel) {
        save(channel);
    }

    @Override
    public void delete(UUID id) {
        Path path = makePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("채널 삭제에 실패하였습니다.");
        }
    }
}