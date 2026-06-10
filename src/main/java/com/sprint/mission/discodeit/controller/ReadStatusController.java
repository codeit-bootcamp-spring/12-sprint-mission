package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements ReadStatusApi {

  private final ReadStatusService readStatusService;

  @PostMapping()
  @Override
  public ResponseEntity<ReadStatusDto> create(@RequestBody @Valid ReadStatusCreateRequest request) {
    log.debug("ReadStatus 생성 요청: request={}", request);
    ReadStatusDto createdReadStatus = readStatusService.create(request);
    log.info("ReadStatus 생성 응답: readStatusId={}, channelId={}, userId={}, lastReadAt={}",
        createdReadStatus.id(), createdReadStatus.channelId(), createdReadStatus.userId(),
        createdReadStatus.lastReadAt());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdReadStatus);
  }

  @PatchMapping("/{readStatusId}")
  @Override
  public ResponseEntity<ReadStatusDto> update(
      @PathVariable UUID readStatusId,
      @RequestBody @Valid ReadStatusUpdateRequest request) {
    log.debug("ReadStatus 수정 요청: readStatusId={}, request={}", readStatusId, request);
    ReadStatusDto updatedReadStatus = readStatusService.update(readStatusId, request);
    log.info("ReadStatus 수정 응답: readStatusId={}, channelId={}, userId={}, lastReadAt={}",
        readStatusId, updatedReadStatus.channelId(), updatedReadStatus.userId(),
        updatedReadStatus.lastReadAt());
    return ResponseEntity.ok(updatedReadStatus);
  }

  @GetMapping()
  @Override
  public ResponseEntity<List<ReadStatusDto>> findAll(@RequestParam(value = "userId") UUID userId) {
    log.debug("ReadStatus 목록 조회 요청: userId={}", userId);
    List<ReadStatusDto> readStatuses = readStatusService.findAllByUserId(userId);
    log.info("ReadStatus 목록 조회 응답: userId={}, foundCount={}", userId, readStatuses.size());
    return ResponseEntity.ok(readStatuses);
  }
}
