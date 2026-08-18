package com.sprint.mission.discodeit.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class MdcSecurityTaskDecoratorTest {

  private final MdcSecurityTaskDecorator decorator = new MdcSecurityTaskDecorator();

  @AfterEach
  void tearDown() {
    MDC.clear();
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("제출 시점의 MDC와 SecurityContext가 다른 스레드에서도 유지된다")
  void decorate_propagatesContextToAnotherThread() throws Exception {
    MDC.put("requestId", "abc12345");
    Authentication authentication =
        new UsernamePasswordAuthenticationToken("tester", "password");
    SecurityContextHolder.getContext().setAuthentication(authentication);

    AtomicReference<String> capturedRequestId = new AtomicReference<>();
    AtomicReference<Authentication> capturedAuth = new AtomicReference<>();

    Runnable decorated = decorator.decorate(() -> {
      capturedRequestId.set(MDC.get("requestId"));
      capturedAuth.set(SecurityContextHolder.getContext().getAuthentication());
    });

    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(decorated).get(5, TimeUnit.SECONDS);
    executor.shutdown();

    assertThat(capturedRequestId.get()).isEqualTo("abc12345");
    assertThat(capturedAuth.get()).isEqualTo(authentication);
  }

  @Test
  @DisplayName("작업이 끝나면 스레드에 남은 컨텍스트를 지운다")
  void decorate_clearsContextAfterRun() throws Exception {
    MDC.put("requestId", "abc12345");
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("tester", "password"));

    AtomicReference<String> leakedRequestId = new AtomicReference<>("not-run");
    AtomicReference<Authentication> leakedAuth = new AtomicReference<>();

    ExecutorService executor = Executors.newSingleThreadExecutor();
    // 같은 스레드를 재사용해 앞선 작업의 컨텍스트가 남는지 확인한다
    executor.submit(decorator.decorate(() -> {
    })).get(5, TimeUnit.SECONDS);
    executor.submit(() -> {
      leakedRequestId.set(MDC.get("requestId"));
      leakedAuth.set(SecurityContextHolder.getContext().getAuthentication());
    }).get(5, TimeUnit.SECONDS);
    executor.shutdown();

    assertThat(leakedRequestId.get()).isNull();
    assertThat(leakedAuth.get()).isNull();
  }
}
