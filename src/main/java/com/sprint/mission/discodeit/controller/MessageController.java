package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Message> createMessage(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        List<BinaryContentCreateRequest> fileRequests =
                files == null ? List.of() : files.stream().map(file -> {
                    try {
                            return new BinaryContentCreateRequest(
                                    file.getOriginalFilename(),
                                    file.getContentType(),
                                    file.getBytes()
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                                            .toList();
        Message message = messageService.create(messageCreateRequest, fileRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Message> updateMessage(@PathVariable UUID id, @RequestBody MessageUpdateRequest request) {
        Message message = messageService.update(id, request);
        return ResponseEntity.ok(message);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/channels/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAllByChannelId(@PathVariable UUID channelId) {
        List<Message> messageList = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messageList);
    }
}
