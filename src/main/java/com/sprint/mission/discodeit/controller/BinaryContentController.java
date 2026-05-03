package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/binary-content")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  public BinaryContentController(BinaryContentService binaryContentService) {
    this.binaryContentService = binaryContentService;
  }

  @PostMapping
  public BinaryContent createBinaryContent(@RequestBody BinaryContentCreateRequest request) {
    return binaryContentService.create(request);
  }

  @GetMapping("/findAll")
  public ResponseEntity<List<BinaryContent>> findAllBinaryContents() {
    return ResponseEntity.ok(binaryContentService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<BinaryContent> findBinaryContent(@PathVariable("id") UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity.ok(binaryContent);
  }

  @DeleteMapping("/{id}")
  public void deleteBinaryContent(@PathVariable UUID id) {
    binaryContentService.delete(id);
  }
}

