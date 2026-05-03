package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "BinaryContent", description = "BinaryContent API")
@RestController
@RequestMapping("/api/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;


  @Operation(summary = "Create a new binary content")
  @PostMapping
  public ResponseEntity<BinaryContent> create(
      @RequestBody BinaryContentCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(binaryContentService.create(request));
  }


  @Operation(summary = "Find all binary contents")
  @GetMapping("/findAll")
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(@RequestParam List<UUID> ids) {
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(ids));
  }

  @Operation(summary = "Find a binary content by ID")
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.find(binaryContentId));
  }

  @Operation(summary = "Delete a binary content by ID")
  @DeleteMapping("/{binaryContentId}")
  public ResponseEntity<Void> delete(@PathVariable UUID binaryContentId) {
    binaryContentService.delete(binaryContentId);
    return ResponseEntity.noContent().build();
  }
}



