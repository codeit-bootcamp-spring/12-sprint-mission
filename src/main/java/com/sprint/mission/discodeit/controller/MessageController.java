package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController implements MessageApi {

  private final MessageService messageService;
  private final BinaryContentService binaryContentService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<UUID> attachmentIds = new ArrayList<>();

    if (attachments != null && !attachments.isEmpty()) {
      for (MultipartFile file : attachments) {
        try {
          attachmentIds.add(upload(file));
        } catch (IOException e) {
          throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
        }
      }
    }

    MessageCreateRequest serviceRequest = new MessageCreateRequest(
        request.channelId(),
        request.authorId(),
        request.content(),
        attachmentIds
    );

    MessageDto created = messageService.create(serviceRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  // GET /api/messages?channelId=...
  @GetMapping
  public ResponseEntity<List<MessageDto>> getMessageList(@RequestParam UUID channelId) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId));
  }

  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageDto> update(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    MessageUpdateRequest serviceRequest = new MessageUpdateRequest(
        messageId,
        request.content()
    );

    return ResponseEntity.ok(messageService.update(serviceRequest));
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<String> delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  public UUID upload(MultipartFile file) throws IOException {
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
    return savedContent.getId();
  }

}
