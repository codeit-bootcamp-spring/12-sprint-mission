package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

// discodeit.storage.type=local 일 때만 Bean 등록 (저장소 구현체 전환 가능)
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") String rootPath) {
    this.root = Paths.get(rootPath);
  }

  // Bean 초기화 시 루트 디렉토리 자동 생성
  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("스토리지 디렉토리 초기화 실패: " + root, e);
    }
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    try {
      Files.write(resolvePath(id), bytes);
      return id;
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 실패: " + id, e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      return Files.newInputStream(resolvePath(id));
    } catch (IOException e) {
      throw new RuntimeException("파일 읽기 실패: " + id, e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) {
    InputStream is = get(dto.id());
    Resource resource = new InputStreamResource(is);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(dto.contentType()))
        .contentLength(dto.size())
        .header(HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename(dto.fileName()).build().toString())
        .body(resource);
  }

  // {root}/{UUID} 규칙으로 파일 경로 결정 - put/get 모두 동일한 규칙 사용
  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }
}
