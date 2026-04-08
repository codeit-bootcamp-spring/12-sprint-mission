package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;

public class JCFChannelService extends BasicChannelService {
    public JCFChannelService(JCFChannelRepository channelRepository) {
        super(channelRepository);
    }
}
