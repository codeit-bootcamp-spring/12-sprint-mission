package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.exception.file.FileStoreException;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileStore<T> {

  private final File file;
  private final String targetName;

  public FileStore(String filePath, String targetName) {
    this.file = new File(filePath);
    this.targetName = targetName;
  }

  @SuppressWarnings("unchecked")
  public T load() {
    if (!file.exists() || file.length() == 0) {
      return null;
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (T) ois.readObject();
    } catch (EOFException e) {
      return null;
    } catch (IOException | ClassNotFoundException e) {
      throw new FileStoreException(
          targetName,
          "LOAD",
          file.getPath(),
          e
      );
    }
  }

  public void save(T data) {
    File parent = file.getParentFile();
    if (parent != null && !parent.exists()) {
      parent.mkdirs();
    }

    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(data);
    } catch (IOException e) {
      throw new FileStoreException(
          targetName,
          "SAVE",
          file.getPath(),
          e
      );
    }
  }
}
