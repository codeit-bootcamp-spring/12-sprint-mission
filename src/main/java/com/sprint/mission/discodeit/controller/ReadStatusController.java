package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusResponse> create(
            @Valid @RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatusResponse readStatusResponse = readStatusService.create(readStatusCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(readStatusResponse);
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> update(
            @PathVariable UUID readStatusId,
            @Valid @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest
    ) {
        ReadStatusResponse readStatusResponse = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity.ok(readStatusResponse);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(@RequestParam UUID userId) {
        List<ReadStatusResponse> readStatusResponseList = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(readStatusResponseList);
    }
}