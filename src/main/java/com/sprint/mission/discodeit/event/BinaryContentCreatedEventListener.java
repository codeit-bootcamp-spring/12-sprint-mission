package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

// 메타 데이터 저장 트랜잭션이 커밋된 뒤에 바이너리 데이터를 저장한다.
// 오래 걸리는 파일 I/O를 트랜잭션 밖으로 빼내 커넥션 점유 시간을 줄이는 것이 목적이다.
@Slf4j
@RequiredArgsConstructor
@Component
public class BinaryContentCreatedEventListener {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;

  // @TransactionalEventListener의 기본 phase는 AFTER_COMMIT
  // @Async를 붙이면 커밋 직후 요청 스레드를 즉시 놓아주고 파일 저장은 별도 풀에서 진행된다
  @Async("fileUploadTaskExecutor")
  @TransactionalEventListener
  public void on(BinaryContentCreatedEvent event) {
    UUID binaryContentId = event.binaryContentId();
    try {
      binaryContentStorage.put(binaryContentId, event.bytes());
      binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
      log.info("바이너리 데이터 저장 완료: binaryContentId={}", binaryContentId);
    } catch (Exception e) {
      // 이 시점에는 메타 데이터가 이미 커밋되었으므로 예외를 던져도 롤백되지 않는다.
      // 대신 status=FAIL로 남겨 프론트엔드가 실패를 표시할 수 있게 한다.
      log.error("바이너리 데이터 저장 실패: binaryContentId={}", binaryContentId, e);
      binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);
    }
  }
}
