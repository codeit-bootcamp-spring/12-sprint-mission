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
                binaryContent.getMimeType(),
                binaryContent.getData(),
                binaryContent.getFilename()
        );
    }

    @Override
    public BinaryContentResponse findById(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent with id " + id + " not found"));

        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getMimeType(),
                binaryContent.getData(),
                binaryContent.getFilename()
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
                        binaryContent.getMimeType(),
                        binaryContent.getData(),
                        binaryContent.getFilename()
                ))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        binaryContentRepository.deleteById(id);
    }
}
