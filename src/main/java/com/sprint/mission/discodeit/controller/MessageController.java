package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;
  private final ObjectMapper objectMapper;

  @RequestMapping(
      method = RequestMethod.POST,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") String messageCreateRequestJson,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    try {
      MessageCreateRequest messageCreateRequest =
          objectMapper.readValue(messageCreateRequestJson, MessageCreateRequest.class);

      List<BinaryContentCreateRequest> attachmentRequests = Optional.ofNullable(attachments)
          .map(files -> files.stream()
              .filter(file -> !file.isEmpty())
              .map(file -> {
                try {
                  return new BinaryContentCreateRequest(
                      file.getOriginalFilename(),
                      file.getContentType(),
                      file.getBytes()
                  );
                } catch (IOException e) {
                  throw new RuntimeException("첨부 파일 변환 실패", e);
                }
              })
              .toList())
          .orElse(new ArrayList<>());

      Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests);

      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(createdMessage);
    } catch (IOException e) {
      throw new RuntimeException("Message 생성 요청 JSON 파싱 실패", e);
    }
  }

  @RequestMapping(path = "/{messageId}", method = RequestMethod.PATCH)
  public ResponseEntity<Message> update(
      @PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest request
  ) {
    Message updatedMessage = messageService.update(messageId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  @RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Message>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId
  ) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }
}