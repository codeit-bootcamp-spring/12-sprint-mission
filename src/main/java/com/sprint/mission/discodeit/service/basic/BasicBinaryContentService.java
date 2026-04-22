package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.binaryContent.MessageImageCreateRequestDto;
import com.sprint.mission.discodeit.dto.binaryContent.ProfileImageCreateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    public BasicBinaryContentService(
            @Qualifier("jCFBinaryContentRepository") BinaryContentRepository binaryContentRepository
    ) {
        this.binaryContentRepository = binaryContentRepository;
    }


    @Override
    public BinaryContentResponseDto createProfileImage(ProfileImageCreateRequestDto dto) {
        BinaryContent binaryContent = BinaryContent.createProfileImage(dto.userId(), dto.imageContent());
        binaryContentRepository.save(binaryContent);

        return BinaryContentResponseDto.from(binaryContent);
    }

    @Override
    public BinaryContentResponseDto createMessageImage(MessageImageCreateRequestDto dto) {
        BinaryContent binaryContent = BinaryContent.createMessageImage(dto.authorId(), dto.messageId(), dto.imageContent());
        binaryContentRepository.save(binaryContent);

        return BinaryContentResponseDto.from(binaryContent);
    }

    @Override
    public BinaryContentResponseDto find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContent not found with id: " + id));

        return BinaryContentResponseDto.from(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(ids);
        List<BinaryContentResponseDto> dtos = new ArrayList<>();

        for (BinaryContent content : binaryContents) {
            BinaryContentResponseDto dto = BinaryContentResponseDto.from(content);
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("Cannot delete. BinaryContent not found with id: " + id);
        }
        binaryContentRepository.deleteById(id);
    }
}
