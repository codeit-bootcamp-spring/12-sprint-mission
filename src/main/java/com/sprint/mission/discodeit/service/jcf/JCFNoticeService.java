package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Notice;
import com.sprint.mission.discodeit.service.NoticeService;

import java.util.*;

public class JCFNoticeService implements NoticeService {
    private final Map<UUID, Notice> data;

    public JCFNoticeService() {
        this.data = new HashMap<>();
    }

    @Override
    public Notice create(Notice notice) {
        return notice;
    }

    @Override
    public Notice read(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Notice> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Notice update(UUID id, Notice updatedNotice) {
        data.put(id, updatedNotice);
        return updatedNotice;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
