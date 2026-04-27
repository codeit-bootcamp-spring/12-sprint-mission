package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.BinaryContentRequestConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<Message> create(
            @RequestPart MessageCreateRequest messageCreateRequest,
            @RequestPart(required = false) List<MultipartFile> attachments
    ) {
        try {
            List<BinaryContentCreateRequest> fileList =
                attachments == null ? List.of() : attachments.stream()
                    .map(BinaryContentRequestConverter::convert)
                    .flatMap(Optional::stream)
                    .toList();

            Message message = messageService.create(messageCreateRequest, fileList);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public ResponseEntity<Message> update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        try {
            Message message = messageService.update(messageId, messageUpdateRequest);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        try {
            messageService.delete(messageId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }
}
