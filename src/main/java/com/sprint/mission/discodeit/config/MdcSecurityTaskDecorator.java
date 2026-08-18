package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * MDC와 SecurityContext는 모두 ThreadLocal에 저장되므로 비동기 스레드에서는 비어 있다.
 *
 * <p>그대로 두면 비동기 로그에 requestId가 빠져 요청과 후속 작업을 이어붙일 수 없고,
 * {@code @PreAuthorize}가 걸린 메소드를 호출할 때 인증 정보가 없어 거부된다.
 *
 * <p>제출 시점의 값을 복사해 두었다가 실행 직전에 심고, 끝나면 반드시 지운다.
 * 스레드가 풀에 반납되어 재사용되므로, 지우지 않으면 다음 작업이 앞 요청의 사용자 권한을 물려받는다.
 */
public class MdcSecurityTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {
    // 여기는 아직 요청 스레드 - 값을 복사해 둔다
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    SecurityContext securityContext = SecurityContextHolder.getContext();

    return () -> {
      // 여기부터는 풀 스레드
      try {
        if (contextMap != null) {
          MDC.setContextMap(contextMap);
        }
        SecurityContextHolder.setContext(securityContext);
        runnable.run();
      } finally {
        MDC.clear();
        SecurityContextHolder.clearContext();
      }
    };
  }
}
