package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Introduction;
import com.sprint.mission.discodeit.service.IntroductionService;

import java.util.*;

public class JCFIntroductionService implements IntroductionService {
    private final Map<UUID, Introduction> data;

    public JCFIntroductionService() {
        this.data = new HashMap<>();
    }

    @Override
    public Introduction create(Introduction introduction) {
        data.put(introduction.getId(), introduction);
        return introduction;
    }
    @Override
    public Introduction read(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Introduction> readAll() {
        return new ArrayList<>(data.values());
    }
    @Override
    public Introduction update(UUID id, Introduction updatedIntroduction) {
        data.put(id, updatedIntroduction);
        return updatedIntroduction;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);

    }
}
