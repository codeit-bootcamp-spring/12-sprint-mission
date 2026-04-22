package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@Primary
@Service
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;


    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                UUID.randomUUID().toString(),
                request.getBytes(),
                request.getFileName(),
                request.getContentType(),
                Instant.now(),
                null,
                null
        );
        return binaryContentRepository.save(binaryContent);
    }

    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllById(ids);
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent를 찾을 수 없습니다: " + id));
        binaryContentRepository.deleteById(id);

    }
}
