package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelRepo;

    public JCFChannelService(){
        channelRepo = new HashMap<>();
    }

    @Override
    public Channel create(Channel channel) {
        channelRepo.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        return channelRepo.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelRepo.values());
    }

    @Override
    public Channel update(UUID id, String channelName) {
        Channel channel = channelRepo.get(id);

        if(channel != null){
            channel.updateChannelName(channelName);
        }

        return channel;
    }

    @Override
    public void delete(UUID id) {
        channelRepo.remove(id);
    }

    @Override
    public void join(UUID channelId, User user) {
        Channel channel = channelRepo.get(channelId);

        if(channel != null){
            channel.addParticipant(user);
        }

    }

    @Override
    public void leave(UUID channelId, User user) {
        Channel channel = channelRepo.get(channelId);

        if(channel != null){
            channel.removeParticipant(user);
        }
    }
}
