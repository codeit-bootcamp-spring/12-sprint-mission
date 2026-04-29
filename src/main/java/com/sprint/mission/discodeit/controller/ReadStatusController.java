package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/read-statuses")
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ReadStatus createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.GET)
    public ReadStatus findReadStatus(@PathVariable UUID readStatusId) {
        return readStatusService.find(readStatusId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ReadStatus> findAllReadStatusesByUserId(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PATCH)
    public ReadStatus updateReadStatus(@PathVariable UUID readStatusId,
                                       @RequestBody ReadStatusUpdateRequest request) {
        return readStatusService.update(readStatusId, request);
    }

    @RequestMapping(value = "/{readStatusId}", method = RequestMethod.DELETE)
    public void deleteReadStatus(@PathVariable UUID readStatusId) {
        readStatusService.delete(readStatusId);
    }
}
