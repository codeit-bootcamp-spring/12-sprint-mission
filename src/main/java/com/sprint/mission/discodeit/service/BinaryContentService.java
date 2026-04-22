package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.binaryContent.MessageImageCreateRequestDto;
import com.sprint.mission.discodeit.dto.binaryContent.ProfileImageCreateRequestDto;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDto createProfileImage(ProfileImageCreateRequestDto dto);
    BinaryContentResponseDto createMessageImage(MessageImageCreateRequestDto dto);
    BinaryContentResponseDto find(UUID id);
    List<BinaryContentResponseDto> findAllByIdIn(List<UUID> ids);
    void delete(UUID id);
}
