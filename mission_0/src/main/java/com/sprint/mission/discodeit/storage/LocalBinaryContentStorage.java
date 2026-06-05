package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") String rootPath
  ) {
    this.root = Paths.get(rootPath);
  }

  @PostConstruct
  public void init() {
    try {
      if (!Files.exists(root)) {
        Files.createDirectories(root);
        log.info("로컬 스토리지 루트 디렉토리가 생성되었습니다. 경로: {}", root.toAbsolutePath());
      }
    } catch (IOException e) {
      throw new RuntimeException("로컬 스토리지 루트 디렉토리를 초기화하지 못했습니다.", e);
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path targetPath = resolvePath(id);
    try {
      Files.write(targetPath, bytes);
      log.info("파일이 로컬 스토리지에 저장되었습니다. ID: {}", id);
      return id;
    } catch (IOException e) {
      throw new RuntimeException("로컬 스토리지 파일 저장 실패. ID: " + id, e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    Path targetPath = resolvePath(id);
    try {
      if (!Files.exists(targetPath)) {
        throw new IllegalArgumentException("스토리지에 존재하지 않는 파일입니다. ID: " + id);
      }
      return Files.newInputStream(targetPath);
    } catch (IOException e) {
      throw new RuntimeException("로컬 스토리지 파일 읽기 실패. ID: " + id, e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) {
    InputStream inputStream = get(dto.id());
    Resource resource = new InputStreamResource(inputStream);

    String encodedFileName = ContentDisposition.builder("attachment")
        .filename(dto.fileName(), StandardCharsets.UTF_8)
        .build()
        .toString();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, encodedFileName)
        .contentType(MediaType.parseMediaType(dto.contentType()))
        .contentLength(dto.size())
        .body(resource);
  }
}