package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

// 업데이트 쪽 애매함,, 확인 해봐야함. 이것도 UUID id 자동생성 이유 알아야함
public interface MessageService {
    MessageDto create(MessageCreateRequest request);

    MessageDto findById(UUID id);

    List<MessageDto> findAllByChannelId(UUID channelId);

    MessageDto update(MessageUpdateRequest request);

    MessageDto delete(UUID id);

}
