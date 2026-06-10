package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping("/{binaryContentId}")
  @Override
  public ResponseEntity<BinaryContentDto> findById(@PathVariable UUID binaryContentId) {
    log.debug("BinaryContent 조회 요청: binaryContentId={}", binaryContentId);
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
    log.info("BinaryContent 조회 응답: binaryContentId={}, filename={}", binaryContentId,
        binaryContent.fileName());
    return ResponseEntity.ok(binaryContent);
  }

  @GetMapping()
  @Override
  public ResponseEntity<List<BinaryContentDto>> findByIds(
      @RequestParam(value = "binaryContentIds") List<UUID> binaryContentIds) {
    log.debug("BinaryContent 목록 조회 요청: binaryContentIds={}", binaryContentIds);
    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    log.info("BinaryContent 목록 조회 응답: binaryContentIds={}, foundCount={}", binaryContentIds,
        binaryContents.size());
    return ResponseEntity.ok(binaryContents);
  }

  @GetMapping("/{binaryContentId}/download")
  @Override
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    log.debug("BinaryContent 다운로드 요청: binaryContentId={}", binaryContentId);
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
    log.info("BinaryContent 다운로드 준비 응답: binaryContentId={}, filename={}", binaryContentId,
        binaryContent.fileName());
    return binaryContentStorage.download(binaryContent);
  }
}