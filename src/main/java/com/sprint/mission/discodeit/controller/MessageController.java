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
  @PostMapping
  public MessageDto createMessage(@RequestBody MessageDto messageDto) {
    return messageService.send(messageDto);
  }

  // Find Message by ID
  @GetMapping("/{id}")
  public MessageDto getMessage(@PathVariable UUID id) {
    return messageService.find(id);
  }

  // Edit Message
  @PutMapping("/{id}")
  public MessageDto updateMessage(@PathVariable UUID id, @RequestBody MessageDto messageDto) {
    return messageService.update(id, messageDto);
  }

  // Delete Message
  @DeleteMapping("/{id}")
  public MessageDto deleteMessage(@PathVariable UUID id) {
    return messageService.delete(id);
  }

  // Find Message by Channel
  @GetMapping
  public List<MessageDto> getMessagesByChannel(@RequestParam UUID channelId) {
    return messageService.findByChannelId(channelId);
  }
}
