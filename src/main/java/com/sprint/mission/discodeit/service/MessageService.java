package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Message;

public interface MessageService {
	Message create(MessageCreateRequestDto dto, List<BinaryContentCreateRequestDto> binaryContentDtos);

	Message find(UUID messageId);

	List<Message> findAllChannelId(UUID channelId);

	Message update(MessageUpdateRequestDto dto);

	void delete(UUID messageId);
}
