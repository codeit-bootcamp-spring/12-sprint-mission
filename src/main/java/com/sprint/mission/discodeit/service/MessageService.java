package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message save(Message message); // 등록
    Message findById(UUID id); // 조회(단건)
    List<Message> findAll(); // 조회(다건)
    Message update(Message message, UUID loginUserId); // 수정
    void deleteByID(UUID id, UUID loginUserId); // 삭제(단건)
    void deleteAll(); // 삭제(다건)
}
