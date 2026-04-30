package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {
	private final ChannelService channelService;

	@RequestMapping(path = "/public", method = RequestMethod.POST)
	public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
		return ResponseEntity.ok(channelService.create(request));
	}

	@RequestMapping(path = "/private", method = RequestMethod.POST)
	public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
		return ResponseEntity.ok(channelService.create(request));
	}

	@RequestMapping(path = "/public/{channelId}", method = RequestMethod.PATCH)
	public ResponseEntity<Channel> update(
		@PathVariable UUID channelId,
		@RequestBody PublicChannelUpdateRequest request){
		return ResponseEntity.ok(channelService.update(channelId, request));
	}

	@RequestMapping(path = "/{channelId}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable UUID channelId){
		channelService.delete(channelId);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public ResponseEntity<List<ChannelDto>> find(@RequestParam UUID userId) {
		return ResponseEntity.ok(channelService.findAllByUserId(userId));
	}
}
