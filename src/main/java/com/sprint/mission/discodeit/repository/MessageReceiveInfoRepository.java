package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.MessageReceiveInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageReceiveInfoRepository {
    MessageReceiveInfo save(MessageReceiveInfo messageReceiveInfo);
    Optional<MessageReceiveInfo> findById(UUID id);
    List<MessageReceiveInfo> findByUserId(UUID userId);
    void deleteById(UUID id);
}