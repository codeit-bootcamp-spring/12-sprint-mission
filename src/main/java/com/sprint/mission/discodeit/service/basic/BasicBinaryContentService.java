package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("binaryContentService")
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {
        if (request.bytes() == null || request.bytes().length == 0) {
            throw new IllegalArgumentException("업로드할 파일 데이터가 없습니다.");
        }

        if (request.fileName() == null || request.fileName().isBlank()) {
            throw new IllegalArgumentException("파일명은 필수 항목입니다.");
        }

        if (request.contentType() == null || request.contentType().isBlank()) {
            throw new IllegalArgumentException("파일의 MIME 타입(contentType)이 누락되었습니다.");
        }

        if (request.fileSize() <= 0) {
            throw new IllegalArgumentException("파일 크기는 0보다 커야 합니다.");
        }

        return binaryContentRepository.save(new BinaryContent(
                request.fileName(),
                request.contentType(),
                (long) request.bytes().length,
                request.bytes()
        ));
    }

    @Override
    public BinaryContent find(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Binary content id cannot be null (BinaryContentService-find)");
        }

        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent with id " + id + " not found (BinaryContentService-find)"));
    }

    // id가 하나라도 레포지토리에 존재하지 않으면 예외 발생하는데 일단 기능구현에 없어서 이렇게 구현 추후 일치하는것만 반환하도록 수정할 수도 있음
    @Override
    public List<BinaryContent> findByIdIn(List<UUID> binaryContentIds) {
        if (binaryContentIds == null || binaryContentIds.isEmpty()) {
            throw new IllegalArgumentException("Binary content ids cannot be null or empty (BinaryContentService-findById)");
        }

        return binaryContentIds.stream()
                .map(this::find)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Binary content id cannot be null (BinaryContentService-delete)");
        }

        binaryContentRepository.delete(id);
    }
}
