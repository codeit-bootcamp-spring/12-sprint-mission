package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final long presignedUrlExpiration;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration:600}") long presignedUrlExpiration) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
    }

    // BinaryContent id를 key로 bytes 업로드
    @Override
    public UUID put(UUID id, byte[] bytes) {
        try (S3Client s3 = getS3Client()) {
            s3.putObject(
                    PutObjectRequest.builder().bucket(bucket).key(id.toString()).build(),
                    RequestBody.fromBytes(bytes));
        }
        return id;
    }

    // key로 객체를 받아 InputStream 반환 (전체를 읽어 메모리로 → 클라이언트 닫아도 안전)
    @Override
    public InputStream get(UUID id) {
        try (S3Client s3 = getS3Client()) {
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(
                    GetObjectRequest.builder().bucket(bucket).key(id.toString()).build());
            return objectBytes.asInputStream();
        }
    }

    // PresignedUrl로 302 리다이렉트 → 클라이언트가 S3에서 직접 다운로드
    @Override
    public ResponseEntity<Void> download(BinaryContentDto dto) {
        String url = generatePresignedUrl(dto.id().toString(), dto.contentType());
        return ResponseEntity.status(HttpStatus.FOUND)   // 302
                .location(URI.create(url))
                .build();
    }

    // 자격증명 + 리전으로 S3Client 생성
    private S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    // 지정한 key에 대해 다운로드용 PresignedUrl 생성
    private String generatePresignedUrl(String key, String contentType) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build()) {

            GetObjectRequest getReq = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .responseContentType(contentType)
                    .build();

            GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                    .getObjectRequest(getReq)
                    .build();

            PresignedGetObjectRequest presigned = presigner.presignGetObject(presignReq);
            return presigned.url().toString();
        }
    }
}