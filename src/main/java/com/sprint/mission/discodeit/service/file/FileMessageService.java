package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;

public class FileMessageService extends BasicMessageService {
    public FileMessageService(FileMessageRepository messageRepository,
                              FileChannelRepository channelRepository,
                              FileUserRepository userRepository) {
        super(messageRepository, channelRepository, userRepository);
    }
}
