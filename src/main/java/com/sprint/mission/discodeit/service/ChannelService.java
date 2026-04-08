package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel save(Channel channel); // 등록
    Channel findById(UUID id); // 조회(단건)
    List<Channel> findAll(); // 조회(다건)
    Channel update(Channel channel, UUID loginUserId); // 수정
    void deleteById(UUID id, UUID loginUserId); // 삭제(단건)
}
