package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;

import java.util.*;

public class JCFMessageService extends BasicMessageService {
    public JCFMessageService(JCFMessageRepository messageRepository,
                             JCFChannelRepository channelRepository,
                             JCFUserRepository userRepository) {
        super(messageRepository, channelRepository, userRepository);
    }

}
