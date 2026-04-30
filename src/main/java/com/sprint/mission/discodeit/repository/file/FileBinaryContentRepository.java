package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
public class FileBinaryContentRepository implements BinaryContentRepository {

  private final Path directory;
  private final FileLockProvider fileLockProvider;

  public FileBinaryContentRepository(
      @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
      FileLockProvider fileLockProvider
  ) {
    this.directory = Paths.get(System.getProperty("user.dir"), fileDirectory,
        BinaryContent.class.getSimpleName());
    this.fileLockProvider = fileLockProvider;
  }

  private Path resolvePath(UUID id) {
    return FileSerialization.resolvePath(directory, id);
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    return FileSerialization.save(
        directory,
        resolvePath(binaryContent.getId()),
        binaryContent,
        fileLockProvider
    );
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return FileSerialization.findById(directory, resolvePath(id), fileLockProvider, BinaryContent.class);
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return FileSerialization.findAll(directory, fileLockProvider, BinaryContent.class).stream()
        .filter(binaryContent -> ids.contains(binaryContent.getId()))
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
}
