package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> send(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<BinaryContentCreateRequest> attachmentList = List.of();

        if (attachments != null && !attachments.isEmpty()) {
            attachmentList = attachments.stream()
                    .map(attachment -> {
                        try {
                            return new BinaryContentCreateRequest(
                                    attachment.getBytes(),
                                    attachment.getOriginalFilename(),
                                    attachment.getContentType()
                            );
                        } catch (IOException e) {
                            throw new IllegalStateException("Failed to read attachment file", e);
                        }
                    })
                    .toList();
        }

        MessageResponse messageResponse = messageService.create(messageCreateRequest, attachmentList);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        MessageResponse messageResponse = messageService.update(messageId, messageUpdateRequest);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MessageResponse>> findAllByChannelId(@RequestParam UUID channelId) {
        List<MessageResponse> messageResponseList = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messageResponseList);
    }
}