package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.util.FileSerialization;
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
public class FileChannelRepository implements ChannelRepository {

  private final Path directory;
  private final FileLockProvider fileLockProvider;

  public FileChannelRepository(
      @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
      FileLockProvider fileLockProvider
  ) {
    this.directory = Paths.get(System.getProperty("user.dir"), fileDirectory,
        Channel.class.getSimpleName());
    this.fileLockProvider = fileLockProvider;
  }

  private Path resolvePath(UUID id) {
    return FileSerialization.resolvePath(directory, id);
  }

  @Override
  public Channel save(Channel channel) {
    return FileSerialization.save(directory, resolvePath(channel.getId()), channel, fileLockProvider);
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    return FileSerialization.findById(directory, resolvePath(id), fileLockProvider, Channel.class);
  }

  @Override
  public List<Channel> findAll() {
    return FileSerialization.findAll(directory, fileLockProvider, Channel.class);
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
