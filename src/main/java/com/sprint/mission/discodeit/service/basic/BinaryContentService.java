package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;
import com.sprint.mission.discodeit.data.request.message.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.data.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.message.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BinaryContent create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.fileData()
        );

        return binaryContentRepository.save(binaryContent);
    }

    public BinaryContentDto find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id).orElse(null);

        if (binaryContent == null) {
            throw new IllegalArgumentException("BinaryContent가 없습니다.");
        }

        return toDto(binaryContent);
    }

    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(this::toDto)
                .toList();
    }

    public List<BinaryContentDto> findAll() {
        return binaryContentRepository.findAll().stream()
                .map(this::toDto).toList();
    }

    public BinaryContent delete(UUID id) {
        return binaryContentRepository.deleteById(id);
    }

    public BinaryContentDto toDto(BinaryContent binaryContent) {
        return new BinaryContentDto(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getFileData()
        );
    }
}
