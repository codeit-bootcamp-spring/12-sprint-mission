package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  // GET /api/readStatuses?userId=...
  @GetMapping(params = "userId")
  public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam UUID userId) {
    List<ReadStatus> statuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.ok(statuses);
  }

  // POST /api/readStatuses
  @PostMapping
  public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
    ReadStatus created = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }
  
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request
  ) {
    ReadStatus updated = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(updated);
  }
}
