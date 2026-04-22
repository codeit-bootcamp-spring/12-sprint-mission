package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.message.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.util.FileSerialization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final String FILE_PATH = "BinaryContent.ser";

    private final Map<UUID, BinaryContent> data;

    public FileBinaryContentRepository() {
        this.data = new HashMap<>();

        List<BinaryContent> binaryContentList = FileSerialization.loadData(FILE_PATH);
        for (BinaryContent bc : binaryContentList) {
            data.put(bc.getId(), bc);
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();

        for (UUID id : ids) {
            BinaryContent binaryContent = data.get(id);
            if (binaryContent != null) {
                result.add(binaryContent);
            }
        }

        return result;
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public BinaryContent deleteById(UUID id) {
        BinaryContent removed = data.remove(id);

        if (removed == null) {
            throw new IllegalArgumentException("해당 id를 가진 BinaryContent 없음.");
        }

        FileSerialization.saveData(FILE_PATH, data.values().stream().toList());

        return removed;
    }
}