package com.sprint.mission.discodeit.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.storage.RetryableStorageException;
import com.sprint.mission.discodeit.exception.storage.StorageException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

// 실제 S3 호출 없이 재시도 판별과 실패 통지만 검증한다
class S3RetryClassificationTest {

  private final List<Object> published = new ArrayList<>();

  private final S3BinaryContentStorage storage = new S3BinaryContentStorage(
      "access", "secret", "ap-northeast-2", "bucket", 600, published::add);

  private S3Exception s3ExceptionWithStatus(int statusCode) {
    return (S3Exception) S3Exception.builder()
        .statusCode(statusCode)
        .awsErrorDetails(AwsErrorDetails.builder().errorMessage("boom").build())
        .message("boom")
        .build();
  }

  @Test
  @DisplayName("네트워크 오류는 재시도 대상")
  void classify_clientException_isRetryable() {
    RuntimeException classified =
        storage.classify(SdkClientException.create("connection reset"), UUID.randomUUID());

    assertThat(classified).isInstanceOf(RetryableStorageException.class);
  }

  @Test
  @DisplayName("5xx와 429는 재시도 대상")
  void classify_serverErrors_areRetryable() {
    UUID id = UUID.randomUUID();

    assertThat(storage.classify(s3ExceptionWithStatus(500), id))
        .isInstanceOf(RetryableStorageException.class);
    assertThat(storage.classify(s3ExceptionWithStatus(503), id))
        .isInstanceOf(RetryableStorageException.class);
    assertThat(storage.classify(s3ExceptionWithStatus(429), id))
        .isInstanceOf(RetryableStorageException.class);
  }

  @Test
  @DisplayName("403 자격증명 오류는 재시도해도 결과가 같으므로 재시도 대상이 아니다")
  void classify_forbidden_isNotRetryable() {
    RuntimeException classified = storage.classify(s3ExceptionWithStatus(403), UUID.randomUUID());

    assertThat(classified)
        .isInstanceOf(StorageException.class)
        .isNotInstanceOf(RetryableStorageException.class);
  }

  @Test
  @DisplayName("재시도 소진 시 RequestId를 담은 실패 이벤트를 발행하고 예외를 다시 던진다")
  void recoverPut_publishesFailureEventAndRethrows() {
    UUID id = UUID.randomUUID();
    MDC.put("requestId", "7641467e");
    try {
      StorageException cause = new StorageException("put", id);

      assertThatThrownBy(() -> storage.recoverPut(cause, id, new byte[]{1}))
          .isSameAs(cause);

      assertThat(published).hasSize(1);
      S3UploadFailedEvent event = (S3UploadFailedEvent) published.get(0);
      assertThat(event.requestId()).isEqualTo("7641467e");
      assertThat(event.binaryContentId()).isEqualTo(id);
      assertThat(event.taskName()).isEqualTo("S3 파일 업로드");
    } finally {
      MDC.clear();
    }
  }
}
