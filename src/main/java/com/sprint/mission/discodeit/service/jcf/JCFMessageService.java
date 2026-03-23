package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;

    public JCFMessageService(List<Message> data) {
        this.data = data;
    }

    @Override
    public Message save(Message message) {
        data.add(message);
        return null;
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
                update.updateTitle(message.getTitle());
                update.updateContent(message.getContent());
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
