package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
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
public class FileMessageRepository implements MessageRepository {

  private final Path directory;
  private final FileLockProvider fileLockProvider;

  public FileMessageRepository(
      @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
      FileLockProvider fileLockProvider
  ) {
    this.directory = Paths.get(System.getProperty("user.dir"), fileDirectory,
        Message.class.getSimpleName());
    this.fileLockProvider = fileLockProvider;
  }

  private Path resolvePath(UUID id) {
    return FileSerialization.resolvePath(directory, id);
  }

  @Override
  public Message save(Message message) {
    return FileSerialization.save(directory, resolvePath(message.getId()), message, fileLockProvider);
  }

  @Override
  public Optional<Message> findById(UUID id) {
    return FileSerialization.findById(directory, resolvePath(id), fileLockProvider, Message.class);
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return FileSerialization.findAll(directory, fileLockProvider, Message.class).stream()
        .filter(message -> message.getChannelId().equals(channelId))
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
        .forEach(message -> this.deleteById(message.getId()));
  }
}
