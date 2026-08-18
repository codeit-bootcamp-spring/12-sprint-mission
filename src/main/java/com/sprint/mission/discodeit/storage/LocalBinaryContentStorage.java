package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.exception.storage.StorageException;
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

  // 성능 비교 실험용 지연 (미션 11)
  private static final long SIMULATED_DELAY_MILLIS = 3000L;

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
      throw new StorageException("init");
    }
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    // 동기/비동기 처리의 응답 시간 차이를 눈에 보이게 하기 위한 인위적 지연.
    // 실제 원격 스토리지의 업로드 지연을 흉내낸 것으로, 성능 비교용 실험 코드다.
    simulateSlowUpload();
    try {
      Files.write(resolvePath(id), bytes);
      return id;
    } catch (IOException e) {
      throw new StorageException("put", id);
    }
  }

  private void simulateSlowUpload() {
    try {
      Thread.sleep(SIMULATED_DELAY_MILLIS);
    } catch (InterruptedException e) {
      // 인터럽트 상태를 복원하지 않으면 상위 코드가 중단 요청을 알 수 없다
      Thread.currentThread().interrupt();
      throw new StorageException("put");
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      return Files.newInputStream(resolvePath(id));
    } catch (IOException e) {
      throw new StorageException("get", id);
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
