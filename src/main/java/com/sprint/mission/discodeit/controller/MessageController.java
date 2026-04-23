package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // [ ] 메시지를 보낼 수 있다.
    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ResponseEntity<Message> create(
            @RequestPart MessageCreateRequest message,
            @RequestPart List<BinaryContentCreateRequest> binaryContent){
        Message saveMessage = messageService.create(message,binaryContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveMessage);
    }

    // [ ] 메시지를 수정할 수 있다.
    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Message> update(
            @PathVariable UUID id,
            @RequestBody MessageUpdateRequest message){
        Message updateMessage = messageService.update(id,message);
        return ResponseEntity.status(HttpStatus.OK).body(updateMessage);
    }

    // [ ] 메시지를 삭제할 수 있다.
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        messageService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // [ ] 특정 채널의 메시지 목록을 조회할 수 있다.
    @RequestMapping(path = "/channel/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findByChannelId(
            @PathVariable UUID channelId){
        List<Message> list = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
