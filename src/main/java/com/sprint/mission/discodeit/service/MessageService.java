package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests);

  MessageDto find(UUID messageId);

  // cursor: 마지막으로 받은 메시지의 createdAt, null이면 첫 페이지
  // size: 한 번에 가져올 메시지 수
  PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size);

  MessageDto update(UUID messageId, MessageUpdateRequest request);

  void delete(UUID messageId);
}
