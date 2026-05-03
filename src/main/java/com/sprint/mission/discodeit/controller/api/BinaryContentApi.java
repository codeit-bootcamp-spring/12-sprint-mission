package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 관리 API")
public interface BinaryContentApi {

  @Operation(summary = "첨부 파일 조회")
  @GetMapping("/{binaryContentId}")
  ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId);

  @Operation(summary = "여러 첨부 파일 조회")
  @GetMapping
  ResponseEntity<List<BinaryContent>> findAllByIdIn(@RequestParam List<UUID> binaryContentIds);

  @Operation(summary = "파일 업로드")
  @PostMapping(consumes = "multipart/form-data")
  ResponseEntity<BinaryContent> upload(@RequestPart("file") MultipartFile file) throws IOException;
}