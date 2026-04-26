package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Message createMessage(
            @RequestBody MessageCreateRequest messageCreateRequest,
            @RequestBody List<BinaryContentCreateRequest> binaryContentCreateRequests){
        return messageService.create(messageCreateRequest, binaryContentCreateRequests);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.GET)
    public Message findMessage(@PathVariable UUID messageId){
        return messageService.find(messageId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public List<Message> findAllByChannelIdMessage(@PathVariable UUID channelId){
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public Message updateMessage(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request){
        return messageService.update(messageId, request);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public void deleteMessage(@PathVariable UUID messageId){
        messageService.delete(messageId);
    }
}
