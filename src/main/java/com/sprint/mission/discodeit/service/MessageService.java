package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.MessageData;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(MessageData messageData);         // 생성
    Message findMessageByContent(String content);           // 조회
    List<Message> findAllMessage();                         // 조회
    Message changeMessageContent(UUID id, String content);  // 수정
    Message deleteMessage(UUID id);                         // 삭제
}
