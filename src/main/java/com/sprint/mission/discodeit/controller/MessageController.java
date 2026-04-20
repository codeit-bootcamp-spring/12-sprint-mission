package com.sprint.mission.discodeit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.BinaryContentRequestConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Message> createPrivateChannel(
            @ModelAttribute MessageCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        List<BinaryContentCreateRequest> fileList = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                Optional<BinaryContentCreateRequest> binaryContent =
                        BinaryContentRequestConverter.convert(file);

                binaryContent.ifPresent(fileList::add);
            }
        }

        Message message = messageService.create(request, fileList);

        if (message == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @RequestMapping(value = "/{messageId}", method = {RequestMethod.PATCH, RequestMethod.PUT })
    public ResponseEntity<Message> update(
            @PathVariable UUID messageId,
            @ModelAttribute MessageUpdateRequest request) {

        if (messageService.find(messageId) == null) {
            return ResponseEntity.notFound().build();
        }

        Message message = messageService.update(messageId, request);

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @RequestMapping(value = "channel/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAll(@PathVariable UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);

        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
