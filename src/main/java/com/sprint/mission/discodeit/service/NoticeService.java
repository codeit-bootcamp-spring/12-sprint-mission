package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Notice;

import java.util.List;
import java.util.UUID;

public interface NoticeService {
    Notice create(Notice notice);
    Notice read(UUID id);
    List<Notice> readAll();
    Notice update(UUID id, Notice updatedNotice);
    void delete(UUID id);
    }
