package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.common.util.FileSerialization;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

  private final Path directory;
  private final FileLockProvider fileLockProvider;

  public FileReadStatusRepository(
      @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
      FileLockProvider fileLockProvider
  ) {
    this.directory = Paths.get(System.getProperty("user.dir"), fileDirectory,
        ReadStatus.class.getSimpleName());
    this.fileLockProvider = fileLockProvider;
  }

  private Path resolvePath(UUID id) {
    return FileSerialization.resolvePath(directory, id);
  }

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    return FileSerialization.save(directory, resolvePath(readStatus.getId()), readStatus, fileLockProvider);
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    return FileSerialization.findById(directory, resolvePath(id), fileLockProvider, ReadStatus.class);
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return FileSerialization.findAll(directory, fileLockProvider, ReadStatus.class).stream()
        .filter(readStatus -> readStatus.getUserId().equals(userId))
        .toList();
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    return FileSerialization.findAll(directory, fileLockProvider, ReadStatus.class).stream()
        .filter(readStatus -> readStatus.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return FileSerialization.exists(directory, resolvePath(id));
  }

  @Override
  public void deleteById(UUID id) {
    FileSerialization.delete(directory, resolvePath(id), fileLockProvider);
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    this.findAllByChannelId(channelId)
        .forEach(readStatus -> this.deleteById(readStatus.getId()));
  }
}
