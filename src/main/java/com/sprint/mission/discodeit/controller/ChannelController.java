package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  @RequestMapping(value = "createPublic", method = RequestMethod.POST)
  @ResponseBody
  public ChannelDto createPublic(@ModelAttribute PublicChannelCreateRequest request) {
    UUID id = channelService.createPublicChannel(request).getId();
    return channelService.find(id);
  }

  @RequestMapping(value = "createPrivate", method = RequestMethod.POST)
  @ResponseBody
  public ChannelDto createPrivate(@RequestBody PrivateChannelCreateRequest request) {
    UUID id = channelService.createPrivateChannel(request).getId();
    return channelService.find(id);
  }

  @RequestMapping(value = "update", method = RequestMethod.PATCH)
  @ResponseBody
  public ChannelDto update(@ModelAttribute ChannelUpdateRequest request) {
    return channelService.update(request);
  }

  @RequestMapping(value = "delete", method = RequestMethod.DELETE)
  @ResponseBody
  public String delete(@RequestParam UUID channelId) {
    channelService.delete(channelId);
    return "channel deleted : " + channelId;
  }

  @RequestMapping(value = "find", method = RequestMethod.GET)
  @ResponseBody
  public List<ChannelDto> find(@RequestParam UUID userId) {
    return channelService.findAllByUserId(userId);
  }

}
