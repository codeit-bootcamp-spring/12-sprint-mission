package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
    if (attachments == null) {
      return ResponseEntity.status(HttpStatus.CREATED).body(
          messageService.create(messageCreateRequest, Collections.emptyList()));
    }
    List<BinaryContentCreateRequest> binaryContentCreateRequests = attachments.stream()
        .filter(f -> f != null && !f.isEmpty())
        .map(this::resolveProfileRequest)
        .toList();
    return ResponseEntity.status(HttpStatus.CREATED).body(
        messageService.create(messageCreateRequest, binaryContentCreateRequests));
  }

  @PatchMapping("/{messageId}")
  @Override
  public ResponseEntity<MessageDto> update(@PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    return ResponseEntity.ok(messageService.update(messageId, request));
  }

  @DeleteMapping(path = "/{messageId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping()
  @Override
  public ResponseEntity<PageResponse<MessageDto>> findByChannelId(@RequestParam UUID channelId,
      @RequestParam(name = "cursor", required = false) Instant cursor,
      @PageableDefault(size = 50,
          sort = "createdAt",
          direction = Direction.DESC
      ) Pageable pageable) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId, cursor, pageable));
  }

  private BinaryContentCreateRequest resolveProfileRequest(MultipartFile file) {
    if (file.isEmpty()) {
      return null;
    }
    try {
      return new BinaryContentCreateRequest(file.getOriginalFilename(), file.getContentType(),
          file.getBytes());
    } catch (IOException e) {
      throw new RuntimeException("프로필 이미지 파일을 읽는 중 오류가 발생했습니다.", e);
    }
  }
}
