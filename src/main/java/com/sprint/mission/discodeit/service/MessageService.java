package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 메시지 생성
    Message create(MessageCreateRequest request);

    // 단건 조회
    Message findById(UUID id);

    // 전체 조회
    List<Message> findAllByChannelId(UUID channelId);

    Message update(UUID id, MessageUpdateRequest request);

    // 삭제
    void delete(UUID id);
}
