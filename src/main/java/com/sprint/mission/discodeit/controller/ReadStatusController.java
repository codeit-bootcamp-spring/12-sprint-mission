package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/channels/{channelId}/read-statuses/create", method = RequestMethod.POST)
    public ResponseEntity<ReadStatus> createReadStatus(
            @PathVariable UUID channelId,
            @RequestBody ReadStatusCreateRequest request) {

        ReadStatusCreateRequest newRequest =
                new ReadStatusCreateRequest(request.userId(), channelId, request.lastReadAt());

        ReadStatus savedReadStatus = readStatusService.create(newRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReadStatus);
    }

    @RequestMapping(value = "/channels/{channelId}/read-statuses/update/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatus> updateReadStatus(
            @PathVariable UUID channelId,
            @PathVariable UUID id,
            @RequestBody ReadStatusUpdateRequest request) {

        ReadStatus updatedReadStatus = readStatusService.updateByChannelId(channelId, id, request);
        return ResponseEntity.ok(updatedReadStatus);
    }

    @RequestMapping(value = "/users/{userId}/read-statuses", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatus>> findAllByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(readStatusService.findAllByUserId(userId)
        );
    }
}
