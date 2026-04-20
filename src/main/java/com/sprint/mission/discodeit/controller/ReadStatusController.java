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

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatus")
public class ReadStatusController {
	private final ReadStatusService readStatusService;

	@RequestMapping(path = "/", method = RequestMethod.POST)
	public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request){
		return ResponseEntity.ok(readStatusService.create(request));
	}

	@RequestMapping(path = "/{readStatusId}", method = RequestMethod.PATCH)
	public ResponseEntity<ReadStatus> update(
		@PathVariable UUID readStatusId,
		@RequestBody ReadStatusUpdateRequest request){
		return ResponseEntity.ok(readStatusService.update(readStatusId, request));
	}

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public ResponseEntity<List<ReadStatus>> findAll(@RequestParam(value = "userId") UUID userId){
		return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
	}
}
