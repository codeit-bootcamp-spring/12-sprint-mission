package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);
    BinaryContent findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
}
