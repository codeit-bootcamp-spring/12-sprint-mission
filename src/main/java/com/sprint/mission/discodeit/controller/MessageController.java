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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ResponseEntity<Message> create(
            @RequestPart MessageCreateRequest message,
            @RequestPart(required = false) List<MultipartFile> files){
        List<BinaryContentCreateRequest> list = files.stream().map(file -> {
                    try {
                        return new BinaryContentCreateRequest(
                                file.getName(),
                                file.getContentType(),
                                file.getBytes()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
        }).toList();
        Message saveMessage = messageService.create(message,list);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveMessage);
    }

    @RequestMapping(path = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Message> update(
            @PathVariable UUID id,
            @RequestBody(required = false) MessageUpdateRequest message){
        Message updateMessage = messageService.update(id,message);
        return ResponseEntity.status(HttpStatus.OK).body(updateMessage);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ){
        messageService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(path = "/channel/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> findByChannelId(
            @PathVariable UUID channelId){
        List<Message> list = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
