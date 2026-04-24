package com.sprint.mission.discodeit.controller;

import java.util.*;
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

        try {
            List<BinaryContentCreateRequest> fileList =
                    files == null ? List.of() : files.stream()
                    .map(BinaryContentRequestConverter::convert)
                    .flatMap(Optional::stream)
                    .toList();

            Message message = messageService.create(request, fileList);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/{messageId}", method = {RequestMethod.PATCH, RequestMethod.PUT })
    public ResponseEntity<Message> update(
            @PathVariable UUID messageId,
            @ModelAttribute MessageUpdateRequest request) {

        try {
            Message message = messageService.update(messageId, request);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        try {
            messageService.delete(messageId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "channel/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAll(@PathVariable UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);

        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
