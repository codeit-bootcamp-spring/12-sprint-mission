package com.sprint.mission.discodeit.storage.s3;

import static java.net.URI.create;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  @Value("${discodeit.storage.s3.presigned-url-expiration:600}")
  private long presignedUrlExpiration;

  public S3BinaryContentStorage(
      @Value("${discodeit.storage.s3.access-key}") String accessKey,
      @Value("${discodeit.storage.s3.secret-key}") String secretKey,
      @Value("${discodeit.storage.s3.region}") String region,
      @Value("${discodeit.storage.s3.bucket}") String bucket
  ) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;

    this.s3Client = getS3Client();
    this.s3Presigner = getS3Presigner();
  }

  @Override
  public UUID put(UUID uuid, byte[] data) {
    try {
      PutObjectRequest putReq = PutObjectRequest.builder()
          .bucket(bucket)
          .key(uuid.toString())
          .build();
      s3Client.putObject(putReq, RequestBody.fromBytes(data));
      return uuid;
    } catch (S3Exception e) {
      log.error("S3 업로드 실패: {}", e.getMessage(), e);
      throw new RuntimeException("S3 업로드 실패: " + uuid, e);
    }
  }

  @Override
  public InputStream get(UUID uuid) {
    try {
      GetObjectRequest getReq = GetObjectRequest.builder()
          .bucket(bucket)
          .key(uuid.toString())
          .build();
      return s3Client.getObject(getReq);
    } catch (S3Exception e) {
      log.error("S3 다운로드 실패: {}", e.getMessage(), e);
      throw new NoSuchElementException("S3 다운로드 실패: " + uuid, e);
    }
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto binaryContentDto) {
    try {
      String presignedUrl = generatePresignedUrl(binaryContentDto.id().toString(),
          binaryContentDto.contentType());
      return ResponseEntity.status(HttpStatus.FOUND)
          .location(create(presignedUrl))
          .build();
    } catch (S3Exception e) {
      log.error("Presigned URL 생성 실패: {}", e.getMessage(), e);
      throw new RuntimeException("Presigned URL 생성 실패", e);
    }
  }

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build();
  }

  private S3Presigner getS3Presigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build();
  }

  private String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest getReq = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(contentType)
        .build();

    GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
        .getObjectRequest(getReq)
        .build();

    return s3Presigner.presignGetObject(presignReq).url().toExternalForm();
  }
}
