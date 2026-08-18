package com.sprint.mission.discodeit.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BinaryContentCreatedEventListenerTest {

  @Mock BinaryContentStorage binaryContentStorage;
  @Mock BinaryContentService binaryContentService;

  @InjectMocks BinaryContentCreatedEventListener listener;

  @Test
  @DisplayName("바이너리 저장에 성공하면 status를 SUCCESS로 변경한다")
  void on_success_marksSuccess() {
    UUID id = UUID.randomUUID();
    byte[] bytes = new byte[]{1, 2, 3};

    listener.on(new BinaryContentCreatedEvent(id, bytes));

    then(binaryContentStorage).should().put(id, bytes);
    then(binaryContentService).should().updateStatus(id, BinaryContentStatus.SUCCESS);
  }

  @Test
  @DisplayName("바이너리 저장에 실패하면 예외를 전파하지 않고 status를 FAIL로 변경한다")
  void on_storageFailure_marksFail() {
    UUID id = UUID.randomUUID();
    byte[] bytes = new byte[]{1, 2, 3};
    willThrow(new RuntimeException("boom")).given(binaryContentStorage).put(any(), any());

    listener.on(new BinaryContentCreatedEvent(id, bytes));

    then(binaryContentService).should().updateStatus(id, BinaryContentStatus.FAIL);
  }
}
