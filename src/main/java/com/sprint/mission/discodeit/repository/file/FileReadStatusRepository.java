package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "spring.service.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY ;
    private final String EXTENSION = ".ser";

    public Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }


    public FileReadStatusRepository(@Value("${storage.location}") String storageLocation) {
        DIRECTORY = Path.of(storageLocation,"ReadStatuses");
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
    public ReadStatus save(ReadStatus readStatus) {
        if (readStatus.getId() == null) throw new NoSuchElementException("ReadStatus 객체를 찾을 수 없습니다.");
        if (readStatus.getUserId() == null) throw new IllegalArgumentException("ReadStatus의 유저 정보가 누락되었습니다.");
        if (readStatus.getChannelId() == null) throw new IllegalArgumentException("ReadStatus의 채널 정보가 누락되었습니다.");

        Path path = makePath(readStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(readStatus);
            return readStatus;
        } catch (IOException e) {
            return null;
        }
    }

    public ReadStatus loadReadStatus (Path path) {
        if(Files.notExists(path) || Files.isDirectory(path)) return null;

        try(FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object obj = ois.readObject();
            if(!(obj instanceof ReadStatus)){
                throw new IllegalArgumentException("파일 내용이 ReadStatus가 아닙니다 : " + path);
            }
            return (ReadStatus) obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("ReadStatus 파일 로드 실패 : " + path);
            return null;
        }
    }

    @Override
    public List<ReadStatus> findAll() {
        if(!Files.isDirectory(DIRECTORY)) return Collections.emptyList();
        try (Stream<Path> stream = Files.list(DIRECTORY)){
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .map(this::loadReadStatus)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(ReadStatus::getUpdatedAt))
                    .toList();
        } catch (IOException e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(loadReadStatus(makePath(id)));
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(status -> status.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .toList();
    }

    @Override
    public Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        return findAll().stream()
                .filter(status -> status.getChannelId().equals(channelId) && status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void delete(UUID id) {
        try{
            boolean deleted = Files.deleteIfExists(makePath(id));
            if(!deleted){
                System.out.println("삭제 실패 : 해당 ID의 파일이 존재하지 않습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생", e);
        }
    }
}
