package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @RequestMapping(value = "/find", method = RequestMethod.GET)
  public ResponseEntity<BinaryContent> find(@RequestParam UUID binaryContentId) {
    BinaryContent content = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(content);
  }

  @RequestMapping(value = "/findAll", method = RequestMethod.GET)
  public ResponseEntity<List<BinaryContent>> findAll(
      @RequestParam(value = "ids") List<UUID> binaryContentIds) {
    List<BinaryContent> contents = binaryContentIds.stream()
        .map(binaryContentService::find)
        .toList();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(contents);
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public ResponseEntity<BinaryContent> upload(@RequestParam("file") MultipartFile file)
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
