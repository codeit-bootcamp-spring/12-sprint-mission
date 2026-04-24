package com.sprint.mission.discodeit.repository.impl;

import com.sprint.mission.discodeit.entity.MessageReceiveInfo;
import com.sprint.mission.discodeit.repository.MessageReceiveInfoRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryMessageReceiveInfoRepository implements MessageReceiveInfoRepository {

    private final Map<UUID, MessageReceiveInfo> store = new HashMap<>();

    @Override
    public MessageReceiveInfo save(MessageReceiveInfo entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MessageReceiveInfo> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<MessageReceiveInfo> findByUserId(UUID userId) {
        return store.values().stream()
                .filter(m -> m.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }
}