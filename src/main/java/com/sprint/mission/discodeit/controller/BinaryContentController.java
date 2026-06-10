package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
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
@RequestMapping(value = "/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @GetMapping(value = "/{binaryContentId}")
  public ResponseEntity<BinaryContentResponse> getFile(
      @PathVariable("binaryContentId") UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.find(binaryContentId));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<BinaryContentResponse>> getFiles(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
  }

  @GetMapping(path = "/{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    log.info("파일 다운로드 API 요청. binaryContentId={}", binaryContentId);

    BinaryContentResponse binaryContent = binaryContentService.find(binaryContentId);
    return binaryContentStorage.download(binaryContent);
  }

}
