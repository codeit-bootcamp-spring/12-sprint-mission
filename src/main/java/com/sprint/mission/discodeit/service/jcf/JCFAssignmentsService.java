package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Assignments;
import com.sprint.mission.discodeit.service.AssignmentsService;

import java.util.*;

public class JCFAssignmentsService implements AssignmentsService {
    private final Map<UUID, Assignments> data;
    public JCFAssignmentsService() {
        this.data = new HashMap<>();
        }

    @Override
    public Assignments create(Assignments assignments) {
        data.put(assignments.getId(), assignments);
        return assignments;
    }

    @Override
    public Assignments read(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Assignments> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Assignments update(UUID id, Assignments updatedAssignments) {
        data.put(id, updatedAssignments);
        return updatedAssignments;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
