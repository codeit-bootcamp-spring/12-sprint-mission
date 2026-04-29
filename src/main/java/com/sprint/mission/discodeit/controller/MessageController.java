package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateApiRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public Message createMessage(@RequestBody MessageCreateApiRequest request) {
        return messageService.create(
                request.message(),
                request.attachments() == null ? List.of() : request.attachments()
        );
    }

    @RequestMapping(value = "/{nessageId}", method = RequestMethod.GET)
    public Message findMessage(@PathVariable UUID messageId) {
        return messageService.find(messageId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Message> findMessagesByChannelId(@RequestParam UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
    public Message updateMessage(@PathVariable UUID messageId,
                                 @RequestBody MessageUpdateRequest request) {
        return messageService.update(messageId, request);
    }

    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public void deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }
}
