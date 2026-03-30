package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;
    private final ChannelService channelService;
    private final UserService userService;

    public JCFMessageService(ChannelService channelService, UserService userService) {
        this.data = new ArrayList<>();
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public Message save(Message message) {
        if(channelService.findById(message.getCh().getId()) == null){
            System.out.println("Channel information is not exist");
            return null;
        }
        if(userService.findById(message.getAuthor().getId()) == null){
            System.out.println("Author information is not exist");
            return null;
        }
        data.add(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        for(Message message : data){
            if(message.getId().equals(id)) return message;
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return data;
    }

    @Override
    public Message update(Message message) {
        for(Message update : data){
            if(update.getId().equals(message.getId())){
                if(message.getTitle() != null) update.updateTitle(message.getTitle());
                if(message.getContent() != null) update.updateContent(message.getContent());
                return update;
            }
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(message -> message.getId().equals(id));
    }
}
