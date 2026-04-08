package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;

    public JCFChannelService() {
        data = new ArrayList<>();
    }

    // 등록
    @Override
    public Channel save(Channel channel) {
        data.add(channel);
        return channel;
    }

    // 조회(단건)
    @Override
    public Channel findById(UUID id) {
        return data.stream()
                    .filter(channel -> channel.getId().equals(id))
                    .findFirst()
                    .orElse(null);
    }

    // 조회(다건)
    @Override
    public List<Channel> findAll() {
        return data;
    }

    // 수정
    @Override
    public Channel update(Channel channel, UUID loginUserId) {
        Channel channelToUpdate = findById(channel.getId());

        if(channelToUpdate == null) {
            throw new NoSuchElementException("수정할 채널이 없습니다!");
        }

        if(!channelToUpdate.getOwner().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("방장만 채널을 수정할 수 있습니다.");
        }

        channelToUpdate.update(
                channel.getChannelName(),
                channel.getChannelType(),
                channel.getDescription()
        );

        return channelToUpdate;
    }

    // 삭제(단건)
    @Override
    public void deleteById(UUID id, UUID loginUserId) {
        Channel channelToDelete = findById(id);

        if(channelToDelete == null) {
            throw new NoSuchElementException("삭제할 채널이 없습니다!");
        }

        if(!channelToDelete.getOwner().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("방장만 채널을 삭제할 수 있습니다.");
        }

        data.remove(channelToDelete);
    }
}
