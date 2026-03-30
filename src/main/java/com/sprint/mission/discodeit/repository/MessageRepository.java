package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    void save(Message message);

    Message findById(UUID id);

    // 해당 채팅방에 속한 메세지만 가져옴
    List<Message> findAllByChannelId(UUID channelId);

    void update(Message message);

    void delete(UUID id);

}