package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.rmi.server.UID;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Send Message
    @RequestMapping(method = RequestMethod.POST)
    public MessageDto createMessage(@RequestBody MessageDto messageDto) {
        return messageService.send(messageDto);
    }

    // Edit Message
    @RequestMapping(value = "/id", method = RequestMethod.PUT)
    public MessageDto updateMessage(@PathVariable UID id, @RequestBody MessageDto messageDto) {
        return messageService.update(id, messageDto);
    }

    // Delete Message
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public MessageDto deleteMessage(@PathVariable UUID id) {
        return messageService.delete(id);
    }

    // Find Message by User
    @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.GET)
    public List<MessageDto> getMessagesByChannel(@PathVariable UUID channelId) {
        return messageService.findByChannelId(channelId);
    }
}
