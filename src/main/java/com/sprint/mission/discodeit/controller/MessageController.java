package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") @Valid MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
    log.debug("Message 생성 요청: messageCreateRequest={}, attachmentsCount={}", messageCreateRequest,
        attachments != null ? attachments.size() : 0);
    List<BinaryContentCreateRequest> binaryContentCreateRequests =
        Optional.ofNullable(attachments).orElse(Collections.emptyList()).stream()
            .map(this::resolveProfileRequest)
            .toList();
    MessageDto createdMessage = messageService.create(messageCreateRequest,
        binaryContentCreateRequests);
    log.info("Message 생성 응답: messageId={}, content={}", createdMessage.id(),
        createdMessage.content());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
  }

  @PatchMapping("/{messageId}")
  @Override
  public ResponseEntity<MessageDto> update(@PathVariable UUID messageId,
      @RequestBody @Valid MessageUpdateRequest request) {
    log.debug("Message 수정 요청: messageId={}, request={}", messageId, request);
    MessageDto updatedMessage = messageService.update(messageId, request);
    log.info("Message 수정 응답: messageId={}, content={}", messageId, updatedMessage.content());
    return ResponseEntity.ok(updatedMessage);
  }

  @DeleteMapping(path = "/{messageId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    log.debug("Message 삭제 요청: messageId={}", messageId);
    messageService.delete(messageId);
    log.info("Message 삭제 응답: messageId={}", messageId);
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
    log.debug("Message 목록 조회 응답: channelId={}, cursor={}, pageable={}", channelId, cursor,
        pageable);
    PageResponse<MessageDto> response = messageService.findAllByChannelId(channelId, cursor,
        pageable);
    log.info("Message 목록 조회 응답: channelId={}, returnedCount={}, hasNext={}", channelId,
        response.content().size(), response.hasNext());
    return ResponseEntity.ok(response);
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
