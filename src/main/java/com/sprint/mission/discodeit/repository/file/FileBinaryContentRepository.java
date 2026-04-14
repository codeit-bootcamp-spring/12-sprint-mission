package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "spring.service.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path DIRECTORY = Path.of(System.getProperty("user.dir"), "data", "BinaryContents");
    private final String EXTENSION = ".ser";

    public Path makePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    public FileBinaryContentRepository() {
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
    public BinaryContent save(BinaryContent content) {
        if (content == null) throw new NoSuchElementException("BinaryContent 객체가 비어있습니다.");
        if (content.getId() == null) throw new NoSuchElementException("BinaryContent ID를 찾을 수 없습니다.");

        Path path = makePath(content.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(content);
            return content;
        } catch (IOException e) {
            return null;
        }
    }

    public BinaryContent loadBinaryContent(Path path) {
        if(Files.notExists(path) || Files.isDirectory(path)) return null;

        try(FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis)
        ){
            Object obj = ois.readObject();
            if (!( obj instanceof BinaryContent)){
                throw new IllegalArgumentException("파일 내용이 BinaryContent가 아닙니다 :" + path);
            }
            return (BinaryContent) obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 입출력 에러");
        }
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if(Files.notExists(DIRECTORY)) return Collections.emptyList();
        Map<UUID,BinaryContent> contentMap = new HashMap<>();
        try (Stream<Path> stream = Files.list(DIRECTORY)){
            stream.filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .forEach(path ->{
                        BinaryContent content = loadBinaryContent(path);
                        contentMap.put(content.getId(),content);
                    });
        } catch (IOException e) {
            throw new NoSuchElementException("경로를 찾을 수 없습니다.");
        }

        return ids.stream()
                .map(contentMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(loadBinaryContent(makePath(id)));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            boolean deleted = Files.deleteIfExists(makePath(id));
            if(!deleted){
                System.out.println("삭제 실패 : 해당 ID의 파일이 존재하지 않습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류 발생", e);
        }
    }
}
