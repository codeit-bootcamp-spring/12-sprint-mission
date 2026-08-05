package com.sprint.mission.discodeit.storage.impl;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.file.BinaryContentFileNotFoundException;
import com.sprint.mission.discodeit.exception.file.FileStorageException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path:local_storage}") String directory
  ) {
    this.root = Path.of(directory);
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw FileStorageException.initError(root, e);
    }
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes, String contentType) {
    Path path = resolvePath(binaryContentId);

    try {
      Files.write(path, bytes);
    } catch (IOException e) {
      throw new FileStorageException(e);
    }

    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    Path path = resolvePath(binaryContentId);

    if (Files.notExists(path)) {
      throw BinaryContentFileNotFoundException.withBinaryContentId(binaryContentId);
    }

    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new FileStorageException(e);
    }
  }

  //  TODO: 다운로드 완료를 로깅으로 찍고 싶으면 인터셉터를 이용하는 방법.
//          일단은 resource 생성후 응답 완료로 처리
  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    InputStream inputStream = get(binaryContentDto.id());

    Resource resource = new InputStreamResource(inputStream);

    log.info(
        "BinaryContent 응답 생성 완료: binaryContentId={}, contentType={}, size={}",
        binaryContentDto.id(),
        binaryContentDto.contentType(),
        binaryContentDto.size()
    );

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + binaryContentDto.fileName() + "\""
        )
        .contentType(
            MediaType.parseMediaType(binaryContentDto.contentType())
        )
        .contentLength(binaryContentDto.size())
        .body(resource);
  }

  @Override
  public void delete(UUID binaryContentId) {
    Path path = resolvePath(binaryContentId);

    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new FileStorageException(e);
    }
  }

  public Path resolvePath(UUID binaryContentId) {
    return root.resolve(binaryContentId.toString());
  }
}
