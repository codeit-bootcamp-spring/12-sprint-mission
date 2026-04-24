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
    @RequestMapping(method = RequestMethod.POST)
    public MessageReceiveInfoDto create(@RequestBody MessageReceiveInfoDto dto) {
        return messageReceiveInfoService.create(dto);
    }

    // Update Specific Channel's Message Receive Info
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public MessageReceiveInfoDto update(@PathVariable UUID id, @RequestBody MessageReceiveInfoDto dto) {
        return messageReceiveInfoService.update(id, dto);
    }

    // Find Specific User's Message Receive Info
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public List<MessageReceiveInfoDto> getByUserId(@PathVariable UUID userId) {
        return messageReceiveInfoService.findByUserId(userId);
    }
}





