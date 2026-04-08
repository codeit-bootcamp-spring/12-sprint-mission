package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.CreateChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(CreateChannelRequest request);        // 생성
    Channel findChannelByName(String name);                     // 조회
    List<Channel> findAllChannels();                            // 조회
    Channel changeChannelName(UUID id, String name);            // 수정
    Channel deleteChannel(UUID id);                             // 삭제
}
