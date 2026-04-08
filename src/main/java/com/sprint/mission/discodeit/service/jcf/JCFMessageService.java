package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;

    public JCFMessageService() { data = new ArrayList<>(); }


    @Override
    public UUID create(Message message) {
        data.add(message);
        return message.getId();
    }

    @Override
    public Optional<Message> findById(UUID id) {
        for (Message message : data) {
            if (message.getId().equals(id)) {
                return Optional.of(message);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Message>> findBySendUser(User user) {
        List<Message> list = new ArrayList<>();
        for (Message message : data) {
            if (message.getSendUser().equals(user)) {
                list.add(message);
            }
        }
        if (!list.isEmpty()) {
            return Optional.of(list);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Message>> findAll() {
        if (!data.isEmpty()) {
            return Optional.of(data);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void updateById(UUID id, String content) {
        findById(id).ifPresentOrElse(
                message -> message.update(content),
                () -> System.out.println("\t수정실패 : 입력된 id(" + id + ")에 해당하는 Message가 없습니다")
        );
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresentOrElse(
                data::remove,
                () -> System.out.println("\t삭제실패 : 입력된 id(" + id + ")에 해당하는 Message가 없습니다")
        );
    }
}
