package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
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
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        log.info(
                "BinaryContent create requested. fileName={}, contentType={}, fileSizeBytes={}",
                request.fileName(),
                request.contentType(),
                request.data().length
        );

        BinaryContent saved = saveBinaryContent(request);

        log.info(
                "BinaryContent created. binaryContentId={}, fileName={}, fileSizeBytes={}",
                saved.getId(),
                saved.getFileName(),
                saved.getSize()
        );

        return binaryContentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BinaryContent createBinaryContent(BinaryContentCreateRequest request) {
        log.info(
                "BinaryContent create requested. fileName={}, contentType={}, fileSizeBytes={}",
                request.fileName(),
                request.contentType(),
                request.data().length
        );

        BinaryContent saved = saveBinaryContent(request);

        log.info(
                "BinaryContent created. binaryContentId={}, fileName={}, fileSizeBytes={}",
                saved.getId(),
                saved.getFileName(),
                saved.getSize()
        );

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public BinaryContentResponse findById(UUID id) {
        BinaryContent binaryContent = getBinaryContentOrThrow(id);
        return binaryContentMapper.toResponse(binaryContent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllById(ids).stream()
                .map(binaryContentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.warn("BinaryContent delete requested. binaryContentId={}", id);

        BinaryContent binaryContent = getBinaryContentOrThrow(id);

        binaryContentRepository.delete(binaryContent);
        binaryContentStorage.delete(id);

        log.info("BinaryContent deleted. binaryContentId={}", id);
    }

    private BinaryContent getBinaryContentOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
    }

    private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = binaryContentMapper.toEntity(request);
        BinaryContent saved = binaryContentRepository.save(binaryContent);

        binaryContentStorage.put(saved.getId(), request.data());

        return saved;
    }
}