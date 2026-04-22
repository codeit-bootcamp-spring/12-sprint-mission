package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@Repository("fileBinaryContentRepository")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileBinaryContentRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "binaryContents");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }


    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path path = makePath(binaryContent.getId());
        boolean result = FileUtils.saveObject(path, binaryContent);
        if (!result) {
            throw new IllegalArgumentException("BinaryContent 저장 실패");
        }
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable((BinaryContent) FileUtils.loadObject(makePath(id)));
    }

    @Override
    public Optional<BinaryContent> findByUserId(UUID userId) {
        List<BinaryContent> binaryContents = findAll();
        for (BinaryContent binaryContent : binaryContents) {
            if (binaryContent.getUserId().equals(userId)) {
                return Optional.of(binaryContent);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UUID> findByMessageId(UUID messageId) {
        List<BinaryContent> binaryContents = findAll();
        List<UUID> binaryContentIds = new ArrayList<>();
        for (BinaryContent binaryContent : binaryContents) {
            if (binaryContent.getMessageId().equals(messageId)) {
                binaryContentIds.add(binaryContent.getId());
            }
        }
        return binaryContentIds;
    }

    @Override
    public List<BinaryContent> findAll() {
        return FileUtils.load(DIRECTORY);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> results = new ArrayList<>();

        if (ids == null || ids.isEmpty()) {
            return results;
        }
        for (UUID id : ids) {
            Path path = makePath(id);
            if (Files.exists(path)) {
                BinaryContent binaryContent = (BinaryContent) FileUtils.loadObject(path);
                results.add(binaryContent);
            }
        }
        return results;
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = makePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = makePath(id);
        if (!Files.exists(path)) {
            throw new RuntimeException("삭제 실패: 해당 " + id + "의 파일을 찾을 수 없습니다.");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일을 삭제할 수 없습니다.");
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<BinaryContent> binaryContents = findAll();
        for (BinaryContent binaryContent : binaryContents) {
            if (!binaryContent.getUserId().equals(userId)) {
                continue;
            }
            Path path = makePath(binaryContent.getId());
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException("파일을 삭제할 수 없습니다. binaryContentId: " + binaryContent.getId());
            }
        }

    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        List<BinaryContent> binaryContents = findAll();
        for (BinaryContent binaryContent : binaryContents) {
            if (!binaryContent.getMessageId().equals(messageId)) {
                continue;
            }
            Path path = makePath(binaryContent.getId());
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException("파일을 삭제할 수 없습니다. binaryContentId: " + binaryContent.getId());
            }
        }
    }
}