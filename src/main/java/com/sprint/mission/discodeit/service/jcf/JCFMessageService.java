package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> data;

    public JCFMessageService() {
        data = new ArrayList<>();
    }

    // 등록
    @Override
    public Message save(Message message) {
        data.add(message);
        return message;
    }

    // 조회(단건)
    @Override
    public Message findById(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 조회(다건)
    @Override
    public List<Message> findAll() {
        return data;
    }

    // 수정
    @Override
    public Message update(Message message, UUID loginUserId) {
        Message messageUpdate = findById(message.getId());

        if(messageUpdate == null) {
            throw new NoSuchElementException("수정할 메세지가 없습니다!");
        }

        if(!messageUpdate.getId().equals(loginUserId)) {
            throw new IllegalArgumentException("본인만 메세지를 수정할 수 있습니다.");
        }

        messageUpdate.update(message.getContent());
        return messageUpdate;
    }

    // 삭제(단건)
    @Override
    public void deleteByID(UUID id, UUID loginUserId) {
        Message messageToDelete = findById(id);

        if(messageToDelete == null) {
            throw new NoSuchElementException("삭제할 메세지가 없습니다!");
        }

        if(!messageToDelete.getAuthorId().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("본인만 메세지를 삭제할 수 있습니다.");
        }

        data.remove(messageToDelete);
    }

    // 삭제(다건)
    @Override
    public void deleteAll() {
        data.clear();
    }

}
