package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.bytes(),
                request.fileName(),
                request.contentType()
        );

        binaryContentRepository.save(binaryContent);

        return toResponse(binaryContent);
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found"));
        return toResponse(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAll().stream()
                .filter(content -> ids.contains(content.getId()))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("BinaryContent not found");
        }
        binaryContentRepository.deleteById(id);
    }

    private BinaryContentResponse toResponse(BinaryContent binaryContent) {
        return BinaryContentResponse.builder()
                .id(binaryContent.getId())
                .bytes(binaryContent.getBytes())
                .fileName(binaryContent.getFileName())
                .contentType(binaryContent.getContentType())
                .createdAt(binaryContent.getCreatedAt())
                .build();
    }
}