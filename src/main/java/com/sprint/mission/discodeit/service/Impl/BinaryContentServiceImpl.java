package com.sprint.mission.discodeit.service.Impl;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("BinaryContentService")
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentRequest request) {
        BinaryContent content = new BinaryContent(
                request.fileName(),
                request.url()
        );
        BinaryContent savedContent = binaryContentRepository.save(content);
        return convertToResponse(savedContent);
    }

    public BinaryContentResponse convertToResponse(BinaryContent content){
        return new BinaryContentResponse(
                content.getId(),
                content.getFileName(),
                content.getUrl()
        );
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent content = binaryContentRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("존재하지 않는 컨텐츠입니다."));
        return convertToResponse(content);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        if(ids == null || ids.isEmpty()){
            return Collections.emptyList();
        }
        List<BinaryContent> contents = binaryContentRepository.findAllByIdIn(ids);

        if(contents.size() != ids.size()){
            throw new NoSuchElementException("요청한 ID 중 일부를 찾을 수 없습니다.");
        }
        return contents.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("존재하지 않는 컨텐츠입니다."));
        binaryContentRepository.deleteById(id);
    }
}
