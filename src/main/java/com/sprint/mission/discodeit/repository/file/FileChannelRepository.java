package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY = Path.of(System.getProperty("user.dir"), "data", "channels");
    private final String EXTENSION = ".ser";

    public Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    public FileChannelRepository() {
        createDirectory(DIRECTORY);
    }

    public void createDirectory(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Channel save(Channel channel) {
        if (channel == null) throw new NullPointerException("Channel 객체가 비어있습니다.");
        if (channel.getId() == null) throw new IllegalArgumentException("Channel ID를 찾을 수 없습니다.");
        if (channel.getAuthor() == null) throw new IllegalArgumentException("Channel의 작성자 정보가 누락되었습니다.");

        Path path = makePath(channel.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Channel loadChannels(Path path) {
        if (Files.notExists(path) || Files.isDirectory(path)) return null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object obj = ois.readObject();
            if (!(obj instanceof Channel)) {
                throw new IllegalArgumentException("파일 내용이 Channel이 아닙니다.: " + path);
            }
            return (Channel) obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 입출력 에러");
        }
    }

    @Override
    public Channel findById(UUID id) {
        return loadChannels(makePath(id));
    }

    @Override
    public List<Channel> findAll() {
        if (Files.notExists(DIRECTORY)) return Collections.emptyList();
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::loadChannels)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public void delete(UUID id) {
        try {
            boolean deleted = Files.deleteIfExists(makePath(id));
            if (!deleted) {
                System.out.println("삭제 실패 : 해당 ID의 파일이 존재하지 않습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생", e);
        }
    }
}
