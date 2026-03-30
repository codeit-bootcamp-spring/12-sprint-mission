package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 채팅방 개설
    Channel createChannel(String name);

    Channel getChannel(UUID id);
    List<Channel> getAllChannels();

    // 채팅방 이름 변경
    Channel updateChannel(UUID id, String name);

    // 채팅방 삭제
    void deleteChannel(UUID id);
}