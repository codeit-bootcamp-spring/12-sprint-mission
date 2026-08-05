package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.file.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;

/*
  현재 create는 dto로 반환하기 때문에 다른 엔티티나 dto에서 사용하지 않음.
  그렇기 때문에 이 메서드에 로깅을 제외하고 storage쪽과 byte를 만드는 부분에 로깅.
*/

  @Override
  @Transactional
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("request is null.");
    }

    BinaryContent binaryContent = binaryContentRepository.save(
        binaryContentMapper.toEntity(request));

    binaryContentStorage.put(
        binaryContent.getId(),
        request.bytes(),
        binaryContent.getContentType()
    );
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto findById(UUID binaryContentId) {
    if (binaryContentId == null) {
      throw new IllegalArgumentException("binaryContentId is null.");
    }

    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> BinaryContentNotFoundException.withBinaryContentId(binaryContentId));
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    if (binaryContentIds == null) {
      throw new IllegalArgumentException("binaryContentIds is null.");
    }

    return binaryContentRepository.findAllById(binaryContentIds).stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public void delete(UUID binaryContentId) {
    if (binaryContentId == null) {
      throw new IllegalArgumentException("id is null.");
    }

    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw BinaryContentNotFoundException.withBinaryContentId(binaryContentId);
    }
    binaryContentStorage.delete(binaryContentId);
    binaryContentRepository.deleteById(binaryContentId);
  }
}
