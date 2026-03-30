package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 전송
    Message sendMessage(UUID channelId, UUID authorId, String content);

    Message getMessage(UUID id);

    // 특정 채팅방 메시지 내역 가져오기
    List<Message> getMessagesByChannelId(UUID channelId);

    // 메시지 수정
    Message updateMessage(UUID id, String content);

    // 메시지 삭제
    void deleteMessage(UUID id);
}