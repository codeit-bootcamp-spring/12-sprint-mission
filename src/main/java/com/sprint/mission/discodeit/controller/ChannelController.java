package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public Channel createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        return channelService.create(request);
    }

    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public Channel createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        return channelService.create(request);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
    public ChannelDto findChannel(@PathVariable UUID channelId) {
        return channelService.find(channelId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelDto> findAllChannelsByUserId(@RequestParam UUID userId) {
        return channelService.findAllByUserId(userId);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.PATCH)
    public Channel updatePublicChannel(@PathVariable UUID channelId,
                                       @RequestBody PublicChannelUpdateRequest request) {
        return channelService.update(channelId, request);
    }

    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public void deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }
}
