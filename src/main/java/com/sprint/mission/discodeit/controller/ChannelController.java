package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")

public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    // Create Public/Private Channel
    @RequestMapping(method = RequestMethod.POST)
          public ChannelDto createChannel(@RequestBody ChannelDto channelDto) {
        return channelService.create(channelDto);
    }

    // Revise Public/Private Channel
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Channel updateChannel(@PathVariable UUID id, @RequestBody ChannelDto channelDto) {
        return channelService.update(id, channelDto);
 }

    // Delete Channel
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ChannelDto deleteChannel(@PathVariable UUID id) {
        return channelService.delete(id);
    }

    // Find Channel by User
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public List<ChannelDto> getChannelsByUser(@PathVariable UUID userId) {
        return channelService.findChannelsByUser(userId);
    }
}