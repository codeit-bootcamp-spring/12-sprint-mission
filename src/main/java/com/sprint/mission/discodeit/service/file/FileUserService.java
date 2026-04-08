package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class FileUserService extends BasicUserService {

    public FileUserService(FileUserRepository userRepository) {
        super(userRepository);
    }
}
