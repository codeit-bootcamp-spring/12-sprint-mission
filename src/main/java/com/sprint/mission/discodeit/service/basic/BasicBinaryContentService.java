package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public BinaryContentResponse create(BinaryContentCreateRequest request) {
    BinaryContent binaryContent = new BinaryContent(
        request.fileName(),
        request.contentType(),
        (long) request.data().length
    );

    BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(
        savedBinaryContent.getId(),
        request.data()
    );

    return BinaryContentResponse.from(savedBinaryContent);
  }

  @Override
  public BinaryContentResponse find(UUID id) {
    BinaryContent binaryContent = getBinaryContentOrThrow(id);

    log.info("파일 조회 완료. binaryContentId={}, fileName={}, size={}",
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getSize()
    );
    return BinaryContentResponse.from(binaryContent);
  }

  @Override
  public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }

    List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(ids);

    return binaryContents.stream()
        .map(BinaryContentResponse::from)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    getBinaryContentOrThrow(id);
    binaryContentRepository.deleteById(id);
  }

  private BinaryContent getBinaryContentOrThrow(UUID id) {
    return binaryContentRepository.findById(id).orElseThrow(() -> {
      log.warn("파일 조회 실패 - 파일 메타데이터를 찾을 수 없음. binaryContentId={}", id);
      return new BinaryContentNotFoundException(id);
    });
  }

}
