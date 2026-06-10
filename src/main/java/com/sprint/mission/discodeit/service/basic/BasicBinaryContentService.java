package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binaryContent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    log.debug("BinaryContent 생성 시작: fileName={}, contentType={}, bytesLength={}",
        request.fileName(), request.contentType(), request.bytes().length);
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(savedBinaryContent.getId(), bytes);

    log.info("BinaryContent 생성 완료: binaryContentId={}, fileName={}, contentType={}, bytesLength={}",
        savedBinaryContent.getId(), fileName, contentType, bytes.length);
    return binaryContentMapper.toDto(savedBinaryContent);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.debug("BinaryContent 조회 시작: binaryContentId={}", binaryContentId);
    BinaryContentDto binaryContentDto = binaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto)
        .orElseThrow(() -> {
          log.warn("BinaryContent 조회 실패(존재하지 않음): binaryContentId={}", binaryContentId);
          return BinaryContentNotFoundException.withId(binaryContentId);
        });
    log.info("BinaryContent 조회 완료: binaryContentId={}, fileName={}, contentType={}, bytesLength={}",
        binaryContentDto.id(), binaryContentDto.fileName(), binaryContentDto.contentType(),
        binaryContentDto.fileName().length());
    return binaryContentDto;
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    log.debug("BinaryContent 목록 조회 시작: binaryContentIds={}", binaryContentIds);
    List<BinaryContentDto> binaryContentDtos = binaryContentRepository.findAllByIdIn(
            binaryContentIds).stream()
        .map(binaryContentMapper::toDto)
        .toList();
    log.info("BinaryContent 목록 조회 완료: binaryContentIds={}, foundCount={}", binaryContentIds,
        binaryContentDtos.size());
    return binaryContentDtos;
  }

  @Override
  @Transactional
  public void delete(UUID binaryContentId) {
    log.debug("BinaryContent 삭제 시작: binaryContentId={}", binaryContentId);
    if (!binaryContentRepository.existsById(binaryContentId)) {
      log.warn("BinaryContent 삭제 실패(존재하지 않음): binaryContentId={}", binaryContentId);
      throw BinaryContentNotFoundException.withId(binaryContentId);
    }
    binaryContentRepository.deleteById(binaryContentId);
    log.info("BinaryContent 삭제 완료: binaryContentId={}", binaryContentId);
  }
}
