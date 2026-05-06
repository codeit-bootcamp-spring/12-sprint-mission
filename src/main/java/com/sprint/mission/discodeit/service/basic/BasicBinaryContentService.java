package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest dto) {
        BinaryContent binaryContent = new BinaryContent(dto.data(), dto.filename(), dto.mimeType());
        binaryContentRepository.save(binaryContent);

        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFilename(),
                binaryContent.getData().length,
                binaryContent.getMimeType(),
                binaryContent.getData()
        );
    }

    @Override
    public BinaryContentResponse findById(UUID id) {
        BinaryContent binaryContent = getBinaryContentOrThrow(id);

        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFilename(),
                binaryContent.getData().length,
                binaryContent.getMimeType(),
                binaryContent.getData()
        );
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> binaryContentList = binaryContentRepository.findAllByIdIn(ids);

        if(binaryContentList.isEmpty()){
            throw new NoSuchElementException("No binary content found for given ids");
        }

        return binaryContentList.stream()
                .map(binaryContent -> new BinaryContentResponse(
                        binaryContent.getId(),
                        binaryContent.getCreatedAt(),
                        binaryContent.getFilename(),
                        binaryContent.getData().length,
                        binaryContent.getMimeType(),
                        binaryContent.getData()
                ))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        getBinaryContentOrThrow(id);
        binaryContentRepository.deleteById(id);
    }

    private BinaryContent getBinaryContentOrThrow(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));
    }
}
