package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path DIRECTORY;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path directory) {
    this.DIRECTORY = directory;
  }

  @PostConstruct
  public void init() {
    if (Files.notExists(DIRECTORY)) {
      try {
        Files.createDirectories(DIRECTORY);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private Path resolvePath(UUID id) {
    return DIRECTORY.resolve(id.toString());
  }

  public UUID put(UUID uuid, byte[] data) {
    Path path = resolvePath(uuid);
    try (

        FileOutputStream fos = new FileOutputStream(path.toFile())
    ) {
      fos.write(data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return uuid;
  }

  @Override
  public InputStream get(UUID uuid) {
    Path path = resolvePath(uuid);
    if (Files.notExists(path)) {
      throw new NoSuchElementException("File not found: " + uuid);
    }
    try {
      return new FileInputStream(path.toFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
    Resource resource = new FileSystemResource(resolvePath(binaryContentDto.id()));
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + binaryContentDto.fileName() + "\"")
        .body(resource);
  }
}
