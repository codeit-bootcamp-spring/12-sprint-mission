package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Transactional
    public BinaryContent create(BinaryContentCreateRequest request) {
        byte[] bytes = request.bytes();

        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                (long) bytes.length,
                request.contentType()
        );
        binaryContentStorage.put(binaryContent.getId(), bytes);
        return binaryContentRepository.save(binaryContent);
    }

    @Override
    public BinaryContent find(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + "not found"));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return binaryContentRepository.findAllById(ids);
    }

    @Override
    @Transactional
    public void delete(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                        .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + binaryContentId + "not found"));
        binaryContentRepository.delete(binaryContent);
    }
}
