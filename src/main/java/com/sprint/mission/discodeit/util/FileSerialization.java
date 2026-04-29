package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.repository.file.FileLockProvider;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public final class FileSerialization {

  private static final String EXTENSION = ".ser";

  private FileSerialization() {
  }

  public static Path resolvePath(Path directory, Object id) {
    return directory.resolve(id + EXTENSION);
  }

  public static void ensureDirectoryExists(Path directory) {
    if (Files.notExists(directory)) {
      try {
        Files.createDirectories(directory);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static <T> T save(Path directory, Path path, T data, FileLockProvider fileLockProvider) {
    ensureDirectoryExists(directory);

    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();

    try (
        FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(data);
      return data;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  public static <T> Optional<T> findById(Path directory, Path path, FileLockProvider fileLockProvider, Class<T> type) {
    ensureDirectoryExists(directory);

    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();

    try {
      if (Files.notExists(path)) {
        return Optional.empty();
      }

      return Optional.of(readLocked(path, type));
    } finally {
      lock.unlock();
    }
  }

  public static <T> List<T> findAll(Path directory, FileLockProvider fileLockProvider, Class<T> type) {
    ensureDirectoryExists(directory);

    try (Stream<Path> paths = Files.list(directory)) {
      return paths
          .filter(path -> path.toString().endsWith(EXTENSION))
          .map(path -> read(path, fileLockProvider, type))
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean exists(Path directory, Path path) {
    ensureDirectoryExists(directory);
    return Files.exists(path);
  }

  public static void delete(Path directory, Path path, FileLockProvider fileLockProvider) {
    ensureDirectoryExists(directory);

    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();

    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  private static <T> T read(Path path, FileLockProvider fileLockProvider, Class<T> type) {
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();

    try {
      return readLocked(path, type);
    } finally {
      lock.unlock();
    }
  }

  private static <T> T readLocked(Path path, Class<T> type) {
    try (
        FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis)
    ) {
      Object object = ois.readObject();
      return type.cast(object);
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
