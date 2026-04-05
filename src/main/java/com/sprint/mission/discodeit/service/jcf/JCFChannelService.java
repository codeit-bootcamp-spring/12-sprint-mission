package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    // 채널 데이터를 저장하는 공간
    private final Map<UUID, Channel> data;

    // 생성자에서 Map 초기화
    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    // 채널 생성 ( 저장 )
    @Override
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    // ID로 채널 하나 조회
    @Override
    public Channel findById(UUID id){
        return data.get(id);
    }

    // 모든 채널 조회
    @Override
    public List<Channel> findAll(){
        return new ArrayList<>(data.values());
    }

    // 채널 수정
    @Override
    public Channel update(UUID id, Channel channel){
        data.put(id, channel);
        return channel;
    }

    // 채널 삭제
    @Override
    public void delete(UUID id){
        data.remove(id);
    }

}
