package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Introduction;

import java.util.List;
import java.util.UUID;

public interface IntroductionService {
    Introduction create(Introduction introduction);
    Introduction read(UUID id);
    List<Introduction> readAll();
    Introduction update(UUID id, Introduction updatedIntroduction);
    void delete(UUID id);
}
