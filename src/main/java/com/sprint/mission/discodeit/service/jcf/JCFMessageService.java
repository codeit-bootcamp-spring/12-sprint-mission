package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final JCFChannelService cs;
    private final JCFUserService us;

    public JCFMessageService(JCFChannelService cs, JCFUserService us) {
        data = new HashMap<>();
        this.cs = cs;
        this.us = us;
    }

    @Override
    public Message save(Message message) {
        if (message == null || us.findById(message.getUserId()) == null || cs.findById(message.getChannelId()) == null) {
            return null; // 각 오류별로 구분해서 알림을 주는 방법도 고민해봐야 할듯, 일단은 null 반환으로 통일
        }

        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID messageId, String content, UUID channelId) {
        Message message = findById(messageId);

        if (message != null) {
            message.update(content, channelId);
        }

        return message;
    }

    @Override
    public Message delete(UUID messageId) {
        return data.remove(messageId);
    }
    // 메세지는 그냥 삭제하면 되는데 유저와 채널이 삭제되었을때 남아있는 메세지는 어떻게 삭제할지?
    // ex) 유저는 삭제된 유저는 [삭제된 유저]로 메세지는 남기고 채널 삭제시 메세지는 전부 삭제?
    // 채널서비스에도 메세지 서비스에 대한 의존성을 주입하려 했지만 권장하지 않는 방식이라 들음 어떻게 처리할지
}
