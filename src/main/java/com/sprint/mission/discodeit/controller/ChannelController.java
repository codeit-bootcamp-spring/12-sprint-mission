package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
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
  @PostMapping
  public ChannelDto createChannel(@RequestBody ChannelDto channelDto) {
    return channelService.create(channelDto);
  }

  // Find Channel by ID
  @GetMapping("/{id}")
  public ChannelDto getChannel(@PathVariable UUID id) {
    return channelService.find(id);
  }

  // Revise Public/Private Channel
  @PutMapping("/{id}")
  public Channel updateChannel(@PathVariable UUID id, @RequestBody ChannelDto channelDto) {
    return channelService.update(id, channelDto);
  }

  // Partial Update Channel
  @PatchMapping("/{id}")
  public Channel patchChannel(@PathVariable UUID id,
      @RequestBody PublicChannelUpdateRequest request) {
    return channelService.update(id, new ChannelDto(request.newName(), null));
  }

  // Delete Channel
  @DeleteMapping("/{id}")
  public ChannelDto deleteChannel(@PathVariable UUID id) {
    return channelService.delete(id);
  }

  // Find Channel by User
  @GetMapping
  public List<ChannelDto> getChannelsByUser(@RequestParam UUID userId) {
    return channelService.findChannelsByUser(userId);
  }
}