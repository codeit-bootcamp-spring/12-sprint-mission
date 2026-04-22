package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> binaryContentMap;

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        binaryContentMap.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(binaryContentMap.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(binaryContentMap.values());
    }

    @Override
    public List<BinaryContent> findByIdIn(UUID id) {
        List<BinaryContent> result = new ArrayList<>();
        for (BinaryContent binaryContent : binaryContentMap.values()) {
            if (binaryContent.getId().equals(id)) {
                result.add(binaryContent);
            }
        }
        return result;
    }

    @Override
    public List<BinaryContent> findAllByMessageId(UUID messageId) {
        List<BinaryContent> result = new ArrayList<>();
        for (BinaryContent binaryContent : binaryContentMap.values()) {
            if (binaryContent.getMessageId().equals(messageId)) {
                result.add(binaryContent);
            }
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        binaryContentMap.remove(id);
    }
}