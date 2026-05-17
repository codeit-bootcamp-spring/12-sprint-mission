package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class MessageController implements MessageApi {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<MessageDto> create(@RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
                                          @RequestPart(value = "attachments", required = false)List<MultipartFile> attachments) {
        List<BinaryContentCreateRequest> attachmentRequests = toAttachmentCreateRequests(attachments);
        Message createdMessage = messageService.create(messageCreateRequest, attachmentRequests);
        MessageDto response = messageMapper.toDto(createdMessage);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping(path = "{messageId}")
    @Override
    public ResponseEntity<MessageDto> update(@PathVariable("messageId") UUID messageId,
                                          @RequestBody MessageUpdateRequest request) {
        Message updatedMessage = messageService.update(messageId, request);
        MessageDto response = messageMapper.toDto(updatedMessage);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping(path = "{messageId}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("messageId") UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(@RequestParam("channelId") UUID channelId,
                                                               @RequestParam(value = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(
                page,
                50,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        PageResponse<MessageDto> response = messageService.findAllByChannelId(channelId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    private List<BinaryContentCreateRequest> toAttachmentCreateRequests(List<MultipartFile> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return List.of();
        }
        return attachments.stream()
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
            throw new RuntimeException(e);
        }
    }
}
