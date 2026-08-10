package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.storage.RetryableStorageException;
import com.sprint.mission.discodeit.exception.storage.StorageException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private static final String TASK_NAME = "S3 파일 업로드";

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final long presignedUrlExpiration;
    private final ApplicationEventPublisher eventPublisher;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration:600}") long presignedUrlExpiration,
            ApplicationEventPublisher eventPublisher) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
        this.eventPublisher = eventPublisher;
    }

    // BinaryContent id를 key로 bytes 업로드.
    // 일시적 장애(RetryableStorageException)만 재시도한다. 자격증명 오류처럼 몇 번을 시도해도
    // 결과가 같은 실패까지 재시도하면 스레드만 붙잡고 실패 인지가 늦어진다.
    @Retryable(
            retryFor = RetryableStorageException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 5000))
    @Override
    public UUID put(UUID id, byte[] bytes) {
        try (S3Client s3 = getS3Client()) {
            s3.putObject(
                    PutObjectRequest.builder().bucket(bucket).key(id.toString()).build(),
                    RequestBody.fromBytes(bytes));
        } catch (SdkException e) {
            throw classify(e, id);
        }
        return id;
    }

    /**
     * 재시도가 모두 실패했거나 애초에 재시도 대상이 아니었던 실패의 종착점.
     *
     * <p>비동기 스레드에서 터진 예외는 사용자 응답에 실리지 않으므로, 사후 디버깅에 필요한 정보를
     * 이벤트로 남겨 관리자에게 통지한다. 그 뒤 예외를 다시 던져 호출자가 status=FAIL을 기록하게 한다.
     */
    @Recover
    public UUID recoverPut(StorageException e, UUID id, byte[] bytes) {
        // TaskDecorator가 옮겨 심어준 값. 실패한 업로드를 원래 요청 로그와 이어붙이는 열쇠다.
        String requestId = MDC.get("requestId");
        log.error("{} 실패 (재시도 소진): requestId={}, binaryContentId={}",
                TASK_NAME, requestId, id, e);

        eventPublisher.publishEvent(
                new S3UploadFailedEvent(TASK_NAME, requestId, id, e.getMessage()));
        throw e;
    }

    // 다시 시도해서 결과가 달라질 수 있는 실패인지 판별한다
    RuntimeException classify(SdkException e, UUID id) {
        if (e instanceof SdkClientException) {
            // 연결 실패/타임아웃 등 서버에 닿지도 못한 경우
            return new RetryableStorageException("put", id);
        }
        if (e instanceof AwsServiceException awsException) {
            int statusCode = awsException.statusCode();
            // 5xx는 S3 쪽 일시 장애, 429는 스로틀링 - 둘 다 잠시 뒤에는 성공할 수 있다
            if (statusCode >= 500 || statusCode == 429) {
                return new RetryableStorageException("put", id);
            }
        }
        // 403(자격증명/권한), 404(버킷 없음) 같은 4xx는 재시도해도 같은 결과다
        return new StorageException("put", id);
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