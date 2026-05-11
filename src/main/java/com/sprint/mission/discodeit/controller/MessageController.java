package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @RequestMapping(value = "create", method = RequestMethod.POST)
  @ResponseBody
  public MessageDto create(@RequestBody MessageCreateRequest request) {
    return messageService.create(request);
  }

  @RequestMapping(value = "update", method = RequestMethod.PATCH)
  @ResponseBody
  public MessageDto update(@RequestBody MessageUpdateRequest request) {
    return messageService.update(request);
  }

  @RequestMapping(value = "delete", method = RequestMethod.DELETE)
  @ResponseBody
  public String delete(@RequestParam UUID messageId) {
    messageService.delete(messageId);
    return "message deleted : " + messageId;
  }

  @RequestMapping(value = "messageList", method = RequestMethod.GET)
  @ResponseBody
  public List<MessageDto> getMessageList(@RequestParam UUID channelId) {
    return messageService.findAllByChannelId(channelId);
  }

}
