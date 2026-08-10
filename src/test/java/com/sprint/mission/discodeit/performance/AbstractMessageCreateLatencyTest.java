package com.sprint.mission.discodeit.performance;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 메시지 생성 API의 응답 지연을 재는 공통 로직.
 *
 * <p>LocalBinaryContentStorage.put에 3초 지연이 심어져 있으므로, 바이너리 저장이 요청 처리와
 * 같은 스레드에서 일어나면 응답도 3초 이상 걸린다. 비동기로 분리되면 요청은 즉시 반환되고
 * 저장은 별도 풀에서 진행된다.
 *
 * <p>일반 통합 테스트는 {@code @Transactional}이라 롤백되어 AFTER_COMMIT 리스너가 아예 실행되지
 * 않는다. 여기서는 실제 커밋이 필요하므로 클래스 레벨 트랜잭션을 쓰지 않는다.
 */
@ActiveProfiles("test")
abstract class AbstractMessageCreateLatencyTest {

  @Autowired MessageService messageService;
  @Autowired UserRepository userRepository;
  @Autowired ChannelRepository channelRepository;
  @Autowired BinaryContentRepository binaryContentRepository;
  @Autowired TransactionTemplate transactionTemplate;

  record Seed(UUID userId, UUID channelId) {

  }

  protected Seed seed(String suffix) {
    return transactionTemplate.execute(status -> {
      User user = userRepository.save(
          new User("latency-" + suffix, "latency-" + suffix + "@test.com", "encoded", null));
      Channel channel = channelRepository.save(
          new Channel(ChannelType.PUBLIC, "latency-channel-" + suffix, null));
      return new Seed(user.getId(), channel.getId());
    });
  }

  /** 첨부 1개를 붙여 메시지를 생성하고, 호출자가 대기한 시간(ms)을 반환한다. */
  protected long measureCreateMillis(Seed seed) {
    MockMultipartFile file = new MockMultipartFile(
        "attachments", "latency.txt", "text/plain", "hello".getBytes());
    BinaryContentCreateRequest attachment = new BinaryContentCreateRequest(
        file.getOriginalFilename(), file.getContentType(), "hello".getBytes());

    long startedAt = System.nanoTime();
    messageService.create(
        new MessageCreateRequest("지연 측정용 메시지", seed.channelId(), seed.userId()),
        List.of(attachment));
    return (System.nanoTime() - startedAt) / 1_000_000;
  }

  /** 지정한 시간 안에 업로드가 SUCCESS로 끝났는지 폴링해서 확인한다. */
  protected boolean awaitAllUploadsSucceeded(long timeoutMillis) throws InterruptedException {
    long deadline = System.currentTimeMillis() + timeoutMillis;
    while (System.currentTimeMillis() < deadline) {
      boolean allDone = binaryContentRepository.findAll().stream()
          .allMatch(content -> content.getStatus() == BinaryContentStatus.SUCCESS);
      if (allDone) {
        return true;
      }
      Thread.sleep(100);
    }
    return false;
  }
}
