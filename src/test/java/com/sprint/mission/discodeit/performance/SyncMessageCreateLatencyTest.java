package com.sprint.mission.discodeit.performance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * 동기 처리와의 비교군.
 *
 * <p>{@code @EnableAsync}를 끄는 대신 두 executor를 SyncTaskExecutor로 갈아끼운다.
 * SyncTaskExecutor는 제출한 스레드에서 그대로 실행하므로, @Async가 없는 것과 같은 결과가 되면서
 * 프로덕션 코드는 한 줄도 건드리지 않는다.
 */
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@Import(SyncMessageCreateLatencyTest.SyncExecutorConfig.class)
class SyncMessageCreateLatencyTest extends AbstractMessageCreateLatencyTest {

  @TestConfiguration
  static class SyncExecutorConfig {

    @Bean("eventTaskExecutor")
    TaskExecutor eventTaskExecutor() {
      return new SyncTaskExecutor();
    }

    @Bean("fileUploadTaskExecutor")
    TaskExecutor fileUploadTaskExecutor() {
      return new SyncTaskExecutor();
    }
  }

  @Test
  @DisplayName("동기: 3초 업로드가 끝날 때까지 응답이 막힌다")
  void create_isBlockedByUpload() {
    Seed seed = seed("sync");

    long elapsed = measureCreateMillis(seed);

    System.out.printf("[동기] 메시지 생성 응답 시간: %d ms%n", elapsed);
    assertThat(elapsed).isGreaterThanOrEqualTo(3_000);
  }
}
