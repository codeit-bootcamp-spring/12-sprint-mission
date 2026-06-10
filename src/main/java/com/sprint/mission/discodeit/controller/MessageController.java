package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> createMessage(
      @Valid @RequestPart("messageCreateRequest") MessageCreateRequest request,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    log.info("메시지 생성 API 요청. channelId={}, authorId={}, attachmentCount={}",
        request.channelId(),
        request.authorId(),
        getAttachmentCount(attachments)
    );

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(messageService.create(request, attachments));
  }

  @Override
  @PatchMapping(value = "/{messageId}")
  public ResponseEntity<MessageResponse> updateMessage(@PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    log.info("메시지 수정 API 요청. messageId={}",
        messageId
    );

    return ResponseEntity.ok(messageService.update(messageId, request));
  }

  @Override
  @DeleteMapping(value = "/{messageId}")
  public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId) {
    log.info("메시지 삭제 API 요청. messageId={}",
        messageId
    );

    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping
  public ResponseEntity<PageResponse<MessageResponse>> findMessagesByChannelId(
      @RequestParam("channelId") UUID channelId, @RequestParam(defaultValue = "0") int page) {
    return ResponseEntity.ok(messageService.findAllByChannelId(channelId, page));
  }

  private int getAttachmentCount(List<MultipartFile> attachments) {
    if (attachments == null) {
      return 0;
    }

    return attachments.size();
  }

}
