package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Assignments;

import java.util.List;
import java.util.UUID;

public interface AssignmentsService {
    Assignments create(Assignments assignments);
    Assignments read(UUID id);
    List<Assignments> readAll();
    Assignments update(UUID id, Assignments updatedAssignments);
    void delete(UUID id);

}

