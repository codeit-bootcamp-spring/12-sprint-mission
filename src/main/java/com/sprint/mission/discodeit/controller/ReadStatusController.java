package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponse> create(@RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatusResponse readStatusResponse = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusResponse);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusResponse> update(
            @PathVariable UUID id,
            @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
    ) {
        ReadStatusResponse readStatusResponse = readStatusService.update(id, readStatusUpdateRequest);
        return ResponseEntity.ok(readStatusResponse);
    }

    @RequestMapping(value = "/findAll/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@PathVariable UUID id) {
        List<ReadStatusResponse> readStatusResponseList = readStatusService.findAllByUserId(id);
        return ResponseEntity.ok(readStatusResponseList);
    }
}