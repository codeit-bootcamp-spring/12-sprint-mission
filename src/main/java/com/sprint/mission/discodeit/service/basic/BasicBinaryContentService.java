package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    // DB에는 메타 정보만 저장, bytes는 커밋 이후 리스너가 storage에 저장
    BinaryContent binaryContent = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType()
    );
    BinaryContent saved = binaryContentRepository.save(binaryContent);
    eventPublisher.publishEvent(new BinaryContentCreatedEvent(saved.getId(), request.bytes()));
    log.info("BinaryContent saved: id={}, fileName={}", saved.getId(), request.fileName());
    return binaryContentMapper.toDto(saved);
  }

  // 이벤트 리스너는 커밋 이후에 실행되므로, 여기서 REQUIRED를 쓰면 이미 완료된
  // 트랜잭션에 참여해 변경이 flush되지 않는다. 반드시 새 트랜잭션에서 수행한다.
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
    binaryContent.updateStatus(status);
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto find(UUID binaryContentId) {
    return binaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto)
        .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw new BinaryContentNotFoundException(binaryContentId);
    }
    binaryContentRepository.deleteById(binaryContentId);
    log.info("BinaryContent deleted: id={}", binaryContentId);
  }
}
