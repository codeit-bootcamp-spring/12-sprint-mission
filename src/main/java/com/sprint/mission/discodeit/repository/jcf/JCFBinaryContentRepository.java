package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository("jCFBinaryContentRepository")
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data;

    public JCFBinaryContentRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        this.data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<BinaryContent> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(content -> userId.equals(content.getUserId()))
                .findFirst();
    }

    @Override
    public List<UUID> findByMessageId(UUID messageId) {
        return data.values().stream()
                .filter(content -> messageId.equals(content.getMessageId()))
                .map(BinaryContent::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<BinaryContent> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> results = new ArrayList<>();

        if (ids != null) {
            for (UUID id : ids) {
                BinaryContent content = this.data.get(id);
                if (content != null) {
                    results.add(content);
                }
            }
        }

        return results;
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.values().removeIf(content -> userId.equals(content.getUserId()));
    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        data.values().removeIf(content -> messageId.equals(content.getMessageId()));
    }
}
