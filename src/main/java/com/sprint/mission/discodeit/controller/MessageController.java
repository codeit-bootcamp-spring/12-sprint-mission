package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService){
        this.messageService = messageService;
    }

    // 메시지 생성
    @RequestMapping(method = RequestMethod.POST)
    public Message create(
            @RequestBody MessageCreateRequest request
    ){
        return messageService.create(request,new ArrayList<>());
    }

    // 메시지 단건 조회
    @RequestMapping(value="/{messageId}", method = RequestMethod.GET)
    public Message find(
            @PathVariable UUID messageId
    ){
        return messageService.find(messageId);
    }

    // 채널 메시지 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<Message> findAllByChannelId(
            @RequestParam UUID channelId
    ){
        return messageService.findAllByChannelId(channelId);
    }

    // 수정
    @RequestMapping(value="/{messageId}", method = RequestMethod.PATCH)
    public Message update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ){
        return messageService.update(messageId, request);
    }

    // 삭제
    @RequestMapping(value="/{messageId}", method = RequestMethod.DELETE)
    public void delete(
            @PathVariable UUID messageId
    ){
        messageService.delete(messageId);
    }
}