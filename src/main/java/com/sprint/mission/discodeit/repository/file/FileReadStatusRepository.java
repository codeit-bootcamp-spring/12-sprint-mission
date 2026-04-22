package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.common.FileUtils;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@Repository("fileReadStatusRepository")
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository() {
        this.DIRECTORY = Path.of(System.getProperty("user.dir"), "my_dir", "readStatuses");
        FileUtils.init(DIRECTORY);
    }

    private Path makePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }


    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = makePath(readStatus.getId());
        boolean result = FileUtils.saveObject(path, readStatus);
        if (!result){
            throw new IllegalArgumentException("ReadStatus 저장 실패");
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable((ReadStatus) FileUtils.loadObject(makePath(id)));
    }

    @Override
    public List<UUID> findAllByChannelId(UUID channelId) {
        List<ReadStatus> allReadStatuses = FileUtils.load(DIRECTORY);
        List<UUID> readStatusIds = new ArrayList<>();

        for (ReadStatus readStatus : allReadStatuses) {
            if (readStatus.getChannelId().equals(channelId)) {
                readStatusIds.add(readStatus.getId());
            }
        }
        return readStatusIds;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
       List<ReadStatus> allReadStatuses = FileUtils.load(DIRECTORY);
       List<ReadStatus> readStatuses = new ArrayList<>();
       for (ReadStatus readStatus : allReadStatuses) {
            if (readStatus.getUserId().equals(userId)) {
                readStatuses.add(readStatus);
            }
        }
        return readStatuses;
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = makePath(id);
        return Files.exists(path);
    }

    @Override
    public boolean existsByChannelIdAndUserId(UUID channelId, UUID userId) {
        List<ReadStatus> allReadStatuses = FileUtils.load(DIRECTORY);
        for (ReadStatus readStatus : allReadStatuses) {
            if (readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId)) {
                return true;
            }
        }
        return false;
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
    public void deleteAllByChannelId(UUID channelId) {
        List<ReadStatus> allReadStatuses = FileUtils.load(DIRECTORY);

        for (ReadStatus status : allReadStatuses) {
            if (!status.getChannelId().equals(channelId)) {
                continue;
            }

            Path path = makePath(status.getId());

            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException("ReadStatus 파일 삭제 실패. ID: " + status.getId());
            }
        }

    }
}
