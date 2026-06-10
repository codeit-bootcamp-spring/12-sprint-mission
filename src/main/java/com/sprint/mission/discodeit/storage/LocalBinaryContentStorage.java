package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.file.FileProcessingException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
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

@Slf4j
@Component
@ConditionalOnProperty(
    prefix = "discodeit.storage",
    name = "type",
    havingValue = "local"
)
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") String rootPath
  ) {
    this.root = Path.of(rootPath);
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      log.error("로컬 파일 저장소 초기화 실패. root={}", root, e);

      throw new FileProcessingException(
          "INIT_LOCAL_STORAGE",
          root.toString(),
          e
      );
    }
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    try {
      Path path = resolvePath(id);
      Files.write(path, bytes);
      return id;
    } catch (IOException e) {
      log.error("파일 저장 실패. binaryContentId={}, root={}",
          id,
          root,
          e
      );

      throw new FileProcessingException(
          "PUT_FILE",
          id,
          root.toString(),
          e
      );
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      Path path = resolvePath(id);
      return Files.newInputStream(path);
    } catch (IOException e) {
      log.error("파일 조회 실패. binaryContentId={}, root={}",
          id,
          root,
          e
      );

      throw new FileProcessingException(
          "GET_FILE",
          id,
          root.toString(),
          e
      );
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentResponse binaryContent) {
    InputStream inputStream = get(binaryContent.id());

    InputStreamResource resource = new InputStreamResource(inputStream);

    String encodedFileName = URLEncoder.encode(
        binaryContent.fileName(),
        StandardCharsets.UTF_8
    ).replaceAll("\\+", "%20");

    ContentDisposition contentDisposition = ContentDisposition.attachment()
        .filename(encodedFileName, StandardCharsets.UTF_8)
        .build();

    ResponseEntity<Resource> response = ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(binaryContent.contentType()))
        .contentLength(binaryContent.size())
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        .body(resource);

    log.info("파일 다운로드 응답 생성 완료. binaryContentId={}, fileName={}, size={}",
        binaryContent.id(),
        binaryContent.fileName(),
        binaryContent.size()
    );

    return response;
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }
}
