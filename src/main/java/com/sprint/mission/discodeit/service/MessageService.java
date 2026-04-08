package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 메시지 생성
    Message create(Message message);

    // 단건 조회
    Message findById(UUID id);

    // 전체 조회
    List<Message> findAll();

    Message update(UUID id, Message message);

    // 삭제
    void delete(UUID id);
}
