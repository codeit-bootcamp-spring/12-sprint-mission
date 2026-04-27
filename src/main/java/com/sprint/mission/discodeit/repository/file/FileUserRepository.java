package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
public class FileUserRepository implements UserRepository {

  private final Path directory;
  private final FileLockProvider fileLockProvider;

  public FileUserRepository(
      @Value("${discodeit.repository.file-directory:data}") String fileDirectory,
      FileLockProvider fileLockProvider
  ) {
    this.directory = Paths.get(System.getProperty("user.dir"), fileDirectory,
        User.class.getSimpleName());
    this.fileLockProvider = fileLockProvider;
  }

  private Path resolvePath(UUID id) {
    return FileSerialization.resolvePath(directory, id);
  }

  @Override
  public User save(User user) {
    return FileSerialization.save(directory, resolvePath(user.getId()), user, fileLockProvider);
  }

  @Override
  public Optional<User> findById(UUID id) {
    return FileSerialization.findById(directory, resolvePath(id), fileLockProvider, User.class);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return this.findAll().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst();
  }

  @Override
  public List<User> findAll() {
    return FileSerialization.findAll(directory, fileLockProvider, User.class);
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
  public boolean existsByEmail(String email) {
    return this.findAll().stream()
        .anyMatch(user -> user.getEmail().equals(email));
  }

  @Override
  public boolean existsByUsername(String username) {
    return this.findAll().stream()
        .anyMatch(user -> user.getUsername().equals(username));
  }
}
