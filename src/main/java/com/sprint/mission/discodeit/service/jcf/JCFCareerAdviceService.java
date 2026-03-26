package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.CareerAdvice;
import com.sprint.mission.discodeit.service.CareerAdviceService;

import java.util.*;

public class JCFCareerAdviceService implements CareerAdviceService {
    private final Map<UUID, CareerAdvice> data;

    public JCFCareerAdviceService() {
        this.data = new HashMap<>();
    }

    @Override
    public CareerAdvice create(CareerAdvice careerAdvice) {
        data.put(careerAdvice.getId(), careerAdvice);
        return careerAdvice;
    }

    @Override
    public CareerAdvice read(UUID id) {
        return data.get(id);
    }

    @Override
    public List<CareerAdvice> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public CareerAdvice update(UUID id, CareerAdvice updatedCareerAdvice) {
        data.put(id, updatedCareerAdvice);
        return updatedCareerAdvice;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);

    }
}
