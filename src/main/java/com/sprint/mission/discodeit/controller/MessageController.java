package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentStorageException;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class MessageController {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> send(
            @Valid @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
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
                            throw new BinaryContentStorageException("READ_ATTACHMENT_FILE", e);
                        }
                    })
                    .toList();
        }

        log.info(
                "Message create API requested. channelId={}, authorId={}, attachmentCount={}",
                messageCreateRequest.channelId(),
                messageCreateRequest.authorId(),
                attachmentList.size()
        );

        MessageResponse messageResponse = messageService.create(messageCreateRequest, attachmentList);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageResponse>> findAllByChannelId(
            @RequestParam("channelId") UUID channelId,
            @RequestParam(value = "cursor", required = false) Instant cursor,
            @PageableDefault(
                    size = 50,
                    page = 0,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        PageResponse<MessageResponse> response =
                messageService.findAllByChannelId(channelId, cursor, pageable);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable UUID messageId,
            @Valid @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        log.info("Message update API requested. messageId={}", messageId);

        MessageResponse messageResponse = messageService.update(messageId, messageUpdateRequest);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        log.warn("Message delete API requested. messageId={}", messageId);

        messageService.delete(messageId);
        return ResponseEntity.noContent().build();
    }
}