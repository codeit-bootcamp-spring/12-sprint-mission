package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;

public class FileChannelService extends BasicChannelService {

    public FileChannelService(FileChannelRepository channelRepository) {
        super(channelRepository);
    }
}
