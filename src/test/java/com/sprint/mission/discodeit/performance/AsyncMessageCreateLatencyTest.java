package com.sprint.mission.discodeit.performance;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// 비동기(@EnableAsync 활성) 상태의 응답 지연
@SpringBootTest
class AsyncMessageCreateLatencyTest extends AbstractMessageCreateLatencyTest {

  @Test
  @DisplayName("비동기: 3초 업로드가 응답을 막지 않는다")
  void create_isNotBlockedByUpload() throws InterruptedException {
    Seed seed = seed("async");

    long elapsed = measureCreateMillis(seed);

    System.out.printf("[비동기] 메시지 생성 응답 시간: %d ms%n", elapsed);
    assertThat(elapsed).isLessThan(1_000);
    // 응답은 빨랐지만 업로드 자체는 백그라운드에서 끝까지 수행된다
    assertThat(awaitAllUploadsSucceeded(10_000)).isTrue();
  }
}
