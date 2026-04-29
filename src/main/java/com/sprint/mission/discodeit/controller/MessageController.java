package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(
            @RequestPart MessageCreateRequest request,
            @RequestPart(required = false) List<MultipartFile> attachments
    ) {
        List<BinaryContentCreateRequest> attachmentRequests =
                Optional.ofNullable(attachments)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(file -> {
                            try {
                                return new BinaryContentCreateRequest(
                                        file.getOriginalFilename(),
                                        file.getContentType(),
                                        file.getBytes()
                                );
                            } catch (IOException e) {
                                throw new RuntimeException("첨부파일 처리 실패: " + file.getOriginalFilename(), e);
                            }
                        })
                        .toList();

        Message message = messageService.create(request, attachmentRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PATCH)
    public ResponseEntity<Message> update(
            @RequestParam UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        Message message = messageService.update(messageId, request);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/findAllByChannelId", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
