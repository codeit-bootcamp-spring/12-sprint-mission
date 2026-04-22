package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity<MessageResponse> send(@RequestBody MessageCreateRequest messageCreateRequest) {
        MessageResponse messageResponse = messageService.create(messageCreateRequest, List.of());
        return ResponseEntity.status(HttpStatus.CREATED).body(messageResponse);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<MessageResponse> update(
            @PathVariable UUID id,
            @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        MessageResponse messageResponse = messageService.update(id, messageUpdateRequest);
        return ResponseEntity.ok(messageResponse);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/findAll/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> findAllByChannelId(@PathVariable UUID id) {
        List<MessageResponse> messageResponseList = messageService.findAllByChannelId(id);
        return ResponseEntity.ok(messageResponseList);
    }
}