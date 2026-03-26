package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.CareerAdvice;

import java.util.List;
import java.util.UUID;

public interface CareerAdviceService {
    CareerAdvice create(CareerAdvice careerAdvice);
    CareerAdvice read(UUID id);
    List<CareerAdvice> readAll ();
    CareerAdvice update(UUID id, CareerAdvice updatedCareerAdvice);
    void delete(UUID id);
    }
