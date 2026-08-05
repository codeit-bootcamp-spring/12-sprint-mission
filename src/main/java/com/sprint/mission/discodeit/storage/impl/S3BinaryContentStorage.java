package com.sprint.mission.discodeit.storage.impl;

import com.sprint.mission.discodeit.config.properties.S3StorageProperties;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.file.FileStorageException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3StorageProperties properties;

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  private static final String PREFIX = "attachments";

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes, String contentType) {
    String key = resolveKey(binaryContentId);

    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(properties.bucket())
          .key(key)
          .contentType(contentType)
          .build();

      s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
      log.info("S3 업로드 완료: {}", key);
    } catch (Exception e) {
      throw new FileStorageException(e);
    }

    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    String key = resolveKey(binaryContentId);

    try {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(properties.bucket())
          .key(key)
          .build();

      return s3Client.getObject(getObjectRequest);
    } catch (Exception e) {
      throw new FileStorageException(e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    String key = resolveKey(binaryContentDto.id());
    String presignedUrl = generatePresignedUrl(key, binaryContentDto.contentType());

    log.info(
        "BinaryContent 응답 생성 완료: binaryContentId={}, contentType={}, size={}",
        binaryContentDto.id(),
        binaryContentDto.contentType(),
        binaryContentDto.size()
    );

    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }

  @Override
  public void delete(UUID binaryContentId) {
    String key = resolveKey(binaryContentId);

    try {
      DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
          .bucket(properties.bucket())
          .key(key)
          .build();

      s3Client.deleteObject(deleteReq);
    } catch (Exception e) {
      throw new FileStorageException(e);
    }
  }

  private String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(properties.bucket())
        .key(key)
        .responseContentType(contentType)
        .build();

    GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(properties.presignedUrlExpiration()))
        .getObjectRequest(getObjectRequest)
        .build();

    return s3Presigner.presignGetObject(getObjectPresignRequest).url().toExternalForm();
  }

  private String resolveKey(UUID binaryContentId) {
    return PREFIX + "/" + binaryContentId.toString();
  }
}
