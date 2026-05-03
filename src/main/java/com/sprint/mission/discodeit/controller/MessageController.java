package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Message", description = "Message API")
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  // Create Message
  @Operation(summary = "create a new message")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

    List<BinaryContentCreateRequest> attachmentRequests =
        Optional.ofNullable(attachments).orElse(List.of())
            .stream()
            .map(f -> {
              try {
                return new BinaryContentCreateRequest(
                    f.getOriginalFilename(), f.getContentType(), f.getBytes());
              } catch (IOException e) {
                throw new RuntimeException("파일 처리 중 오류 발생", e);
              }
            })
            .toList();

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(messageService.create(request, attachmentRequests));
  }

  // Find All Messages in the channel
  @Operation(summary = "Find All Messages in the channel")
  @GetMapping
  public ResponseEntity<List<MessageDto>> findAllByChannelId(@RequestParam UUID channelId) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }

  // Edit Message
  @Operation(summary = "Edit a message")
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> update(@PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    return ResponseEntity.ok(messageService.update(messageId, request));
  }


  // Delete Message
  @Operation(summary = "Delete a message")
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }
}


