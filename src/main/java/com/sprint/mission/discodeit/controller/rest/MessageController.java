package com.sprint.mission.discodeit.controller.rest;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import com.sprint.mission.discodeit.exception.file.FileProcessingException;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
@Tag(name = "Message", description = "Message API")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> createWithAttachments(
      @RequestPart("messageCreateRequest") @Valid MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    log.debug(
        "메시지 생성 API 요청: channelId={}, authorId={}, attachmentCount={}, totalAttachmentSize={}",
        messageCreateRequest.channelId(),
        messageCreateRequest.authorId(),
        countFiles(attachments),
        totalSize(attachments)
    );

    List<BinaryContentCreateRequest> attachmentRequests =
        toBinaryContentCreateRequests(attachments);
    MessageDto messageDto = messageService.create(messageCreateRequest, attachmentRequests);

    return ResponseEntity.status(HttpStatus.CREATED).body(messageDto);
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam UUID channelId,
      @RequestParam(required = false) Instant cursor,
      Pageable pageable
  ) {
    return ResponseEntity.ok(
        messageService.findAllByChannelId(channelId, cursor, pageable)
    );
  }

  //  현재 수정이 메시지 수정만 처리해서 Json 업데이트 추가
  @PatchMapping(
      value = "/{messageId}",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<MessageDto> updateMessageJson(
      @PathVariable UUID messageId,
      @RequestBody @Valid MessageUpdateRequest messageUpdateRequest
  ) {
    log.debug("메시지 수정 API 요청: messageId={}, attachmentCount={}, totalAttachmentSize={}",
        messageId
    );

    MessageDto messageDto = messageService.update(messageId,
        messageUpdateRequest, List.of());

    return ResponseEntity.ok(messageDto);
  }

  @PatchMapping(
      value = "/{messageId}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<MessageDto> updateMessageMultipart(
      @PathVariable UUID messageId,
      @RequestPart("messageUpdateRequest") @Valid MessageUpdateRequest messageUpdateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    log.debug("메시지 수정 API 요청: messageId={}, attachmentCount={}, totalAttachmentSize={}",
        messageId,
        countFiles(attachments),
        totalSize(attachments)
    );

    List<BinaryContentCreateRequest> attachmentRequests =
        toBinaryContentCreateRequests(attachments);

    MessageDto messageDto = messageService.update(messageId,
        messageUpdateRequest, attachmentRequests);

    return ResponseEntity.ok(messageDto);
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(
      @PathVariable UUID messageId
  ) {
    log.debug("메시지 삭제 API 요청: messageId={}", messageId);
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  private List<BinaryContentCreateRequest> toBinaryContentCreateRequests(
      List<MultipartFile> files
  ) {
    if (files == null || files.isEmpty()) {
      return List.of();
    }

    return files.stream()
        .filter(file -> file != null && !file.isEmpty())
        .map(this::toBinaryContentCreateRequest)
        .toList();
  }

  private BinaryContentCreateRequest toBinaryContentCreateRequest(MultipartFile file) {
    try {
      return new BinaryContentCreateRequest(
          file.getOriginalFilename(),
          file.getContentType(),
          file.getBytes()
      );
    } catch (IOException e) {
      throw new FileProcessingException(e);
    }
  }

  private int countFiles(List<MultipartFile> files) {
    if (files == null) {
      return 0;
    }

    return (int) files.stream()
        .filter(file -> file != null && !file.isEmpty())
        .count();
  }

  private long totalSize(List<MultipartFile> files) {
    if (files == null) {
      return 0;
    }

    return files.stream()
        .filter(file -> file != null && !file.isEmpty())
        .mapToLong(MultipartFile::getSize)
        .sum();
  }
}
