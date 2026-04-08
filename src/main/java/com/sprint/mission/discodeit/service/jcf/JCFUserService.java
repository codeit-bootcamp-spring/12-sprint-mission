package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JCFUserService extends BasicUserService {
    public JCFUserService(JCFUserRepository userRepository) {
        super(userRepository);
    }
}
