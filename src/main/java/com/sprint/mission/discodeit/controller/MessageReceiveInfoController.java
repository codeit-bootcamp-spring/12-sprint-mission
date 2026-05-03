package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.MessageReceiveInfoDto;
import com.sprint.mission.discodeit.service.MessageReceiveInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message-receive-info")
public class MessageReceiveInfoController {

  private final MessageReceiveInfoService messageReceiveInfoService;


  public MessageReceiveInfoController(MessageReceiveInfoService messageReceiveInfoService) {
    this.messageReceiveInfoService = messageReceiveInfoService;
  }

  // Create Specific Channel's Message Receive Info
  @PostMapping
  public MessageReceiveInfoDto create(@RequestBody MessageReceiveInfoDto dto) {
    return messageReceiveInfoService.create(dto);
  }

  // Find Specific Message Receive Info by ID
  @GetMapping("/{id}")
  public MessageReceiveInfoDto get(@PathVariable UUID id) {
    return messageReceiveInfoService.find(id);
  }

  // Update Specific Channel's Message Receive Info
  @PutMapping("/{id}")
  public MessageReceiveInfoDto update(@PathVariable UUID id,
      @RequestBody MessageReceiveInfoDto dto) {
    return messageReceiveInfoService.update(id, dto);
  }

  // Delete Specific Message Receive Info
  @DeleteMapping("/{id}")
  public MessageReceiveInfoDto delete(@PathVariable UUID id) {
    return messageReceiveInfoService.delete(id);
  }

  // Find Specific User's Message Receive Info
  @GetMapping
  public List<MessageReceiveInfoDto> getByUserId(@RequestParam UUID userId) {
    return messageReceiveInfoService.findByUserId(userId);
  }
}





