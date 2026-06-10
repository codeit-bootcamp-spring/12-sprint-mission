package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

    MessageResponse create(MessageCreateRequest request, List<MultipartFile> attachments);

    PageResponse<MessageResponse> findAllByChannelId(UUID channelId, int page);

    MessageResponse update(UUID messageId, MessageUpdateRequest request);

    void delete(UUID id);
}
