package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;

  // GET /api/binaryContents/{binaryContentId}
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId) {
    BinaryContent content = binaryContentService.find(binaryContentId);
    return ResponseEntity.ok(content);
  }

  @GetMapping()
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam List<UUID> binaryContentIds) {
    List<BinaryContent> contents = binaryContentIds.stream()
        .map(binaryContentService::find)
        .toList();
    return ResponseEntity.ok(contents);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BinaryContent> upload(@RequestPart("file") MultipartFile file)
      throws IOException {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("업로드된 파일이 없습니다.");
    }

    BinaryContentCreateRequest request = new BinaryContentCreateRequest(
        file.getOriginalFilename(),
        file.getContentType(),
        file.getSize(),
        file.getBytes()
    );

    BinaryContent savedContent = binaryContentService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedContent);
  }
}
