package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @PostMapping()
  @Override
  public ResponseEntity<ReadStatusDto> create(@RequestBody ReadStatusCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(request));
  }

  @PatchMapping("/{readStatusId}")
  @Override
  public ResponseEntity<ReadStatusDto> update(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    return ResponseEntity.ok(readStatusService.update(readStatusId, request));
  }

  @GetMapping()
  @Override
  public ResponseEntity<List<ReadStatusDto>> findAll(@RequestParam(value = "userId") UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }
}
