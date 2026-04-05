package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelService() {
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
    public Channel createChannel(String name) {
        Channel channel = new Channel(name);

        Path path = makePath(channel.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            System.out.println("채널 저장에 실패하였습니다.");
        }
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        Path path = makePath(id);
        if (Files.notExists(path)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (Channel) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Channel> getAllChannels() {
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
    public Channel updateChannel(UUID id, String name) {
        Channel channel = getChannel(id);
        if (channel != null) {
            channel.update(name); // 내용 변경

            // 덮어쓰기
            Path path = makePath(id);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
                oos.writeObject(channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        try {
            Files.deleteIfExists(makePath(id));
        } catch (IOException e) {
            System.out.println("채널 삭제에 실패하였습니다.");
        }
    }
}