package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/readstatus")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @RequestMapping(value = "create", method = RequestMethod.POST)
  @ResponseBody
  public String create(@ModelAttribute ReadStatusCreateRequest request) {
    return "created ID : " + readStatusService.create(request).id();
  }

  @RequestMapping(value = "update", method = RequestMethod.PATCH)
  @ResponseBody
  public String update(@ModelAttribute ReadStatusUpdateRequest request) {
    return "updated ID : " + readStatusService.update(request).id();
  }

  @RequestMapping(value = "user", method = RequestMethod.GET)
  @ResponseBody
  public List<ReadStatusDto> findAllByUserId(@RequestParam UUID userId) {
    return readStatusService.findAllByUserId(userId);
  }

}
